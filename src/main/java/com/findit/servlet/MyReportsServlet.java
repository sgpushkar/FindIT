package com.findit.servlet;

import com.findit.dao.ItemDAO;
import com.findit.model.Item;
import com.findit.model.Match;
import com.findit.model.User;
import com.findit.service.MatchService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** The user's dashboard: their own reports plus possible matches. */
@WebServlet("/dashboard")
public class MyReportsServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static final List<String> FILTERS = Arrays.asList("ALL", "LOST", "FOUND", "ACTIVE", "RESOLVED");
    private final ItemDAO itemDAO = new ItemDAO();
    private final MatchService matchService = new MatchService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        String filter = req.getParameter("filter");
        if (filter == null || !FILTERS.contains(filter)) {
            filter = "ALL";
        }

        try {
            List<Item> items = itemDAO.findByUser(user.getId(), filter);

            // Possible matches for every ACTIVE report the user owns
            List<Match> matches = new ArrayList<>();
            for (Item item : itemDAO.findByUser(user.getId(), "ACTIVE")) {
                List<Match> found = matchService.findMatches(item);
                if (!found.isEmpty()) {
                    matches.add(found.get(0)); // best match per report
                }
            }

            req.setAttribute("items", items);
            req.setAttribute("matches", matches);
            req.setAttribute("filter", filter);
            req.setAttribute("filters", FILTERS);
            render(req, resp, "dashboard.jsp");
        } catch (SQLException e) {
            databaseError(req, resp, e);
        }
    }
}
