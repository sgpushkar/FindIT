package com.findit.filter;

import com.findit.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Blocks protected pages unless a user is logged in; /admin also needs the ADMIN role. */
@WebFilter(urlPatterns = {"/report", "/dashboard", "/resolve", "/admin", "/admin/*"})
public class AuthFilter extends HttpFilter {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (user == null) {
            String target = req.getServletPath() + (req.getQueryString() == null ? "" : "?" + req.getQueryString());
            resp.sendRedirect(req.getContextPath() + "/login?next="
                    + URLEncoder.encode(target, StandardCharsets.UTF_8));
            return;
        }

        if (req.getServletPath().startsWith("/admin") && !user.isAdmin()) {
            resp.setStatus(403);
            req.setAttribute("errorTitle", "Access denied");
            req.setAttribute("errorMessage", "This area is only for administrators.");
            req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
            return;
        }

        resp.setHeader("Cache-Control", "no-store");
        chain.doFilter(req, resp);
    }
}
