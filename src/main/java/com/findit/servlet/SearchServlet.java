package com.findit.servlet;

import com.findit.dao.ItemDAO;
import com.findit.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/search")
public class SearchServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String q = Validator.clean(req.getParameter("q"));
        String type = Validator.clean(req.getParameter("type"));
        String category = Validator.clean(req.getParameter("category"));
        String location = Validator.clean(req.getParameter("location"));
        String status = Validator.clean(req.getParameter("status"));

        // Ignore unknown values instead of failing
        if (!Validator.isType(type)) type = "";
        if (!Validator.isCategory(category)) category = "";
        if (!Validator.isStatus(status)) status = "";
        if (q.length() > 100) q = q.substring(0, 100);
        if (location.length() > 100) location = location.substring(0, 100);

        req.setAttribute("q", q);
        req.setAttribute("type", type);
        req.setAttribute("category", category);
        req.setAttribute("location", location);
        req.setAttribute("status", status);
        req.setAttribute("categories", Validator.CATEGORIES);

        try {
            req.setAttribute("results", itemDAO.search(q, type, category, location, status));
            render(req, resp, "search.jsp");
        } catch (SQLException e) {
            databaseError(req, resp, e);
        }
    }
}
