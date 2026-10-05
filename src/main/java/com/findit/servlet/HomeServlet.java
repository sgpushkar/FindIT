package com.findit.servlet;

import com.findit.dao.ItemDAO;
import com.findit.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/** Landing page with hero, quick search and recent reports. */
@WebServlet(urlPatterns = {"/home"})
public class HomeServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("recentItems", itemDAO.findRecent(6));
            req.setAttribute("activeLost", itemDAO.countByType("LOST"));
            req.setAttribute("activeFound", itemDAO.countByType("FOUND"));
            req.setAttribute("resolvedCount", itemDAO.countByStatus("RESOLVED"));
            req.setAttribute("categories", Validator.CATEGORIES);
            render(req, resp, "home.jsp");
        } catch (SQLException e) {
            databaseError(req, resp, e);
        }
    }
}
