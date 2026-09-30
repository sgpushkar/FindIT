package com.findit.servlet;

import com.findit.dao.UserDAO;
import com.findit.model.User;
import com.findit.util.PasswordUtil;
import com.findit.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (currentUser(req) != null) {
            redirect(req, resp, "/dashboard");
            return;
        }
        render(req, resp, "register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = Validator.clean(req.getParameter("name"));
        String email = Validator.clean(req.getParameter("email")).toLowerCase();
        String password = req.getParameter("password");
        String confirm = req.getParameter("confirmPassword");
        req.setAttribute("name", name);
        req.setAttribute("email", email);

        String error = null;
        if (Validator.isBlank(name) || Validator.isBlank(email) || Validator.isBlank(password) || Validator.isBlank(confirm)) {
            error = "All fields are required.";
        } else if (name.length() > 100) {
            error = "Name must be under 100 characters.";
        } else if (!Validator.isEmail(email)) {
            error = "Please enter a valid email address.";
        } else if (password.length() < 6) {
            error = "Password must be at least 6 characters.";
        } else if (!password.equals(confirm)) {
            error = "Passwords do not match.";
        }

        if (error != null) {
            req.setAttribute("error", error);
            render(req, resp, "register.jsp");
            return;
        }

        try {
            if (userDAO.emailExists(email)) {
                req.setAttribute("error", "An account with this email already exists.");
                render(req, resp, "register.jsp");
                return;
            }
            userDAO.create(new User(name, email, PasswordUtil.hash(password)));
            flash(req, "Account created. You can log in now.");
            redirect(req, resp, "/login");
        } catch (SQLException e) {
            databaseError(req, resp, e);
        }
    }
}
