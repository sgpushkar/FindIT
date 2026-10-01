package com.findit.servlet;

import com.findit.dao.ItemDAO;
import com.findit.model.Item;
import com.findit.model.User;
import com.findit.service.MatchService;
import com.findit.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

/** Shows the report form (GET) and saves a new LOST/FOUND report (POST). */
@WebServlet("/report")
public class AddItemServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private final ItemDAO itemDAO = new ItemDAO();
    private final MatchService matchService = new MatchService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type");
        req.setAttribute("type", Validator.isType(type) ? type : "LOST");
        prepareForm(req);
        render(req, resp, "report.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User user = currentUser(req);

        Item item = new Item();
        item.setUserId(user.getId());
        item.setType(Validator.clean(req.getParameter("type")));
        item.setItemName(Validator.clean(req.getParameter("itemName")));
        item.setCategory(Validator.clean(req.getParameter("category")));
        item.setDescription(Validator.clean(req.getParameter("description")));
        item.setLocation(Validator.clean(req.getParameter("location")));
        item.setItemDate(Validator.clean(req.getParameter("itemDate")));

        String error = validate(item);
        if (error != null) {
            req.setAttribute("error", error);
            req.setAttribute("type", Validator.isType(item.getType()) ? item.getType() : "LOST");
            req.setAttribute("form", item);
            prepareForm(req);
            render(req, resp, "report.jsp");
            return;
        }

        try {
            itemDAO.create(item);
            req.setAttribute("item", item);
            req.setAttribute("matches", matchService.findMatches(item));
            render(req, resp, "report-success.jsp");
        } catch (SQLException e) {
            databaseError(req, resp, e);
        }
    }

    private String validate(Item item) {
        if (!Validator.isType(item.getType())) {
            return "Please choose whether you lost or found the item.";
        }
        if (Validator.isBlank(item.getItemName()) || Validator.isBlank(item.getLocation())
                || Validator.isBlank(item.getCategory()) || Validator.isBlank(item.getItemDate())) {
            return "Item name, category, location and date are required.";
        }
        if (item.getItemName().length() > 100 || item.getLocation().length() > 100) {
            return "Item name and location must be under 100 characters.";
        }
        if (item.getDescription().length() > 1000) {
            return "Description must be under 1000 characters.";
        }
        if (!Validator.isCategory(item.getCategory())) {
            return "Please choose a valid category.";
        }
        if (!Validator.isValidPastDate(item.getItemDate())) {
            return "Please enter a valid date that is not in the future.";
        }
        return null;
    }

    private void prepareForm(HttpServletRequest req) {
        req.setAttribute("categories", Validator.CATEGORIES);
        req.setAttribute("today", LocalDate.now().toString());
    }
}
