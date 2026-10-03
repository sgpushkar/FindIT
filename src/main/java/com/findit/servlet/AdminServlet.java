package com.findit.servlet;

import com.findit.dao.ItemDAO;
import com.findit.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/** Admin dashboard: statistics, all reports and all users. Protected by AuthFilter. */
@WebServlet("/admin")
public class AdminServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final ItemDAO itemDAO = new ItemDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("totalUsers", userDAO.count());
            req.setAttribute("totalLost", itemDAO.countByType("LOST"));
            req.setAttribute("totalFound", itemDAO.countByType("FOUND"));
            req.setAttribute("totalActive", itemDAO.countByStatus("ACTIVE"));
            req.setAttribute("totalResolved", itemDAO.countByStatus("RESOLVED"));
            req.setAttribute("items", itemDAO.findAll());
            req.setAttribute("users", userDAO.findAll());
            render(req, resp, "admin.jsp");
        } catch (SQLException e) {
            databaseError(req, resp, e);
        }
    }
}
