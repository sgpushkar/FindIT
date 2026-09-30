package com.findit.servlet;

import com.findit.dao.UserDAO;
import com.findit.model.User;
import com.findit.util.PasswordUtil;
import com.findit.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (currentUser(req) != null) {
            redirect(req, resp, "/dashboard");
            return;
        }
        req.setAttribute("next", safeNext(req.getParameter("next")));
        render(req, resp, "login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = Validator.clean(req.getParameter("email"));
        String password = req.getParameter("password");
        String next = safeNext(req.getParameter("next"));
        req.setAttribute("email", email);
        req.setAttribute("next", next);

        if (Validator.isBlank(email) || Validator.isBlank(password)) {
            req.setAttribute("error", "Please enter your email and password.");
            render(req, resp, "login.jsp");
            return;
        }

        try {
            User user = userDAO.findByEmail(email);
            if (user == null || !PasswordUtil.verify(password, user.getPassword())) {
                req.setAttribute("error", "Incorrect email or password.");
                render(req, resp, "login.jsp");
                return;
            }

            // Prevent session fixation: drop any old session and start a fresh one.
            HttpSession old = req.getSession(false);
            if (old != null) {
                old.invalidate();
            }
            HttpSession session = req.getSession(true);
            user.setPassword(null); // never keep the hash in the session
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(60 * 60);

            flash(req, "Welcome back, " + user.getFirstName() + ".");
            if (!next.isEmpty()) {
                redirect(req, resp, next);
            } else {
                redirect(req, resp, user.isAdmin() ? "/admin" : "/dashboard");
            }
        } catch (SQLException e) {
            databaseError(req, resp, e);
        }
    }

    /** Only allow redirects to local paths (prevents open-redirects). */
    private String safeNext(String next) {
        if (next == null || !next.startsWith("/") || next.startsWith("//") || next.contains("\\")) {
            return "";
        }
        return next;
    }
}
