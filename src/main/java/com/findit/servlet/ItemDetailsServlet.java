package com.findit.servlet;

import com.findit.dao.ItemDAO;
import com.findit.model.Item;
import com.findit.model.User;
import com.findit.service.MatchService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/item")
public class ItemDetailsServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final ItemDAO itemDAO = new ItemDAO();
    private final MatchService matchService = new MatchService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = parseId(req.getParameter("id"));
        if (id < 0) {
            showError(req, resp, 400, "Invalid report", "That report link doesn't look right.");
            return;
        }
        try {
            Item item = itemDAO.findById(id);
            if (item == null) {
                showError(req, resp, 404, "Report not found", "This report may have been removed.");
                return;
            }
            User user = currentUser(req);
            boolean owner = user != null && user.getId() == item.getUserId();
            req.setAttribute("item", item);
            req.setAttribute("isOwner", owner);
            req.setAttribute("canManage", owner || (user != null && user.isAdmin()));
            if (owner && !item.isResolved()) {
                req.setAttribute("matches", matchService.findMatches(item));
            }
            render(req, resp, "item.jsp");
        } catch (SQLException e) {
            databaseError(req, resp, e);
        }
    }
}
