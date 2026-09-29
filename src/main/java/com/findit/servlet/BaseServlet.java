package com.findit.servlet;

import com.findit.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Parent class for all FindIT servlets (demonstrates inheritance).
 * Holds helpers for sessions, views, flash messages and friendly errors.
 */
public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    protected static final String VIEWS = "/WEB-INF/views/";

    protected User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    protected void render(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher(VIEWS + view).forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    protected void flash(HttpServletRequest req, String message) {
        req.getSession().setAttribute("flash", message);
    }

    /** Shows the friendly error page - never a raw stack trace. */
    protected void showError(HttpServletRequest req, HttpServletResponse resp, int status,
                             String title, String message) throws ServletException, IOException {
        resp.setStatus(status);
        req.setAttribute("errorTitle", title);
        req.setAttribute("errorMessage", message);
        render(req, resp, "error.jsp");
    }

    protected void databaseError(HttpServletRequest req, HttpServletResponse resp, Exception e)
            throws ServletException, IOException {
        log("Database error", e);
        showError(req, resp, 500, "Something went wrong",
                "We couldn't reach the database right now. Please try again in a moment.");
    }

    /** Returns a positive id or -1 if the parameter is missing or not a number. */
    protected int parseId(String value) {
        try {
            int id = Integer.parseInt(value == null ? "" : value.trim());
            return id > 0 ? id : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
