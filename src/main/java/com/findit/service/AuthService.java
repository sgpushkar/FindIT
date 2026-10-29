package com.findit.service;

import com.findit.dao.UserDAO;
import com.findit.dto.AuthResponse;
import com.findit.dto.LoginRequest;
import com.findit.dto.RegisterRequest;
import com.findit.model.User;
import com.findit.util.PasswordUtil;
import com.findit.util.Validator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserDAO userDAO;
    private final Map<String, Integer> tokenStore = new ConcurrentHashMap<>();

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public AuthResponse login(LoginRequest req, HttpServletRequest request) throws SQLException {
        String email = Validator.clean(req.getEmail());
        String password = req.getPassword();

        if (Validator.isBlank(email) || Validator.isBlank(password)) {
            throw new IllegalArgumentException("Please enter your email and password.");
        }

        User user = userDAO.findByEmail(email);
        if (user == null || !PasswordUtil.verify(password, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect email or password.");
        }

        user.setPassword(null);

        // Manage session
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);

        // Generate auth token
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, user.getId());

        return new AuthResponse(user, token);
    }

    public User register(RegisterRequest req) throws SQLException {
        String name = Validator.clean(req.getName());
        String email = Validator.clean(req.getEmail()).toLowerCase();
        String password = req.getPassword();
        String confirm = req.getConfirmPassword();

        if (Validator.isBlank(name) || Validator.isBlank(email) || Validator.isBlank(password) || Validator.isBlank(confirm)) {
            throw new IllegalArgumentException("All fields are required.");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Name must be under 100 characters.");
        }
        if (!Validator.isEmail(email)) {
            throw new IllegalArgumentException("Please enter a valid email address.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (!password.equals(confirm)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        User newUser = new User(name, email, PasswordUtil.hash(password));
        userDAO.create(newUser);
        newUser.setPassword(null);
        return newUser;
    }

    public void logout(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            tokenStore.remove(token);
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public User getCurrentUser(HttpServletRequest request) {
        // 1. Check Bearer or X-Auth-Token
        String token = extractToken(request);
        if (token != null && tokenStore.containsKey(token)) {
            int userId = tokenStore.get(token);
            try {
                User user = userDAO.findById(userId);
                if (user != null) {
                    user.setPassword(null);
                    return user;
                }
            } catch (SQLException ignored) {
            }
        }

        // 2. Check HTTP Session
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object obj = session.getAttribute("user");
            if (obj instanceof User user) {
                user.setPassword(null);
                return user;
            }
        }

        return null;
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) {
            return authHeader.substring(7).trim();
        }
        String customHeader = request.getHeader("X-Auth-Token");
        if (customHeader != null && !customHeader.isBlank()) {
            return customHeader.trim();
        }
        return null;
    }
}

