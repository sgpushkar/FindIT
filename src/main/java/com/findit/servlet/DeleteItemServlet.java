package com.findit.servlet;

import com.findit.dao.ItemDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/** Admin-only: permanently deletes an inappropriate report. */
@WebServlet("/admin/delete")
public class DeleteItemServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final ItemDAO itemDAO = new ItemDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = parseId(req.getParameter("id"));
        if (id < 0) {
            showError(req, resp, 400, "Invalid report", "That report ID is not valid.");
            return;
        }
        try {
            if (itemDAO.delete(id)) {
                flash(req, "Report deleted.");
            } else {
                flash(req, "That report no longer exists.");
            }
            redirect(req, resp, "/admin");
        } catch (SQLException e) {
            databaseError(req, resp, e);
        }
    }
}
