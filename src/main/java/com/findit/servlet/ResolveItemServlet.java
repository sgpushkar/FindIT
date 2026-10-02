package com.findit.servlet;

import com.findit.dao.ItemDAO;
import com.findit.model.Item;
import com.findit.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/** Marks a report RESOLVED. Only the owner (or an admin) may do this. */
@WebServlet("/resolve")
public class ResolveItemServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        int id = parseId(req.getParameter("id"));
        if (id < 0) {
            showError(req, resp, 400, "Invalid report", "That report ID is not valid.");
            return;
        }
        try {
            Item item = itemDAO.findById(id);
            if (item == null) {
                showError(req, resp, 404, "Report not found", "This report may have been removed.");
                return;
            }
            if (item.getUserId() != user.getId() && !user.isAdmin()) {
                showError(req, resp, 403, "Not allowed", "You can only update your own reports.");
                return;
            }
            itemDAO.updateStatus(id, "RESOLVED");
            flash(req, "\"" + item.getItemName() + "\" marked as resolved.");
            String back = req.getParameter("back");
            redirect(req, resp, "admin".equals(back) && user.isAdmin() ? "/admin" : "/item?id=" + id);
        } catch (SQLException e) {
            databaseError(req, resp, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        redirect(req, resp, "/dashboard");
    }
}
