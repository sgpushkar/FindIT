package com.findit.service;

import com.findit.dao.ItemDAO;
import com.findit.dao.UserDAO;
import com.findit.dto.ItemRequest;
import com.findit.dto.StatsResponse;
import com.findit.model.Item;
import com.findit.model.Match;
import com.findit.model.User;
import com.findit.util.Validator;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemService {

    private final ItemDAO itemDAO;
    private final UserDAO userDAO;
    private final MatchService matchService;

    public ItemService(ItemDAO itemDAO, UserDAO userDAO, MatchService matchService) {
        this.itemDAO = itemDAO;
        this.userDAO = userDAO;
        this.matchService = matchService;
    }

    public Map<String, Object> create(ItemRequest req, User user) throws SQLException {
        if (user == null) {
            throw new IllegalStateException("Authentication required to report an item.");
        }

        Item item = new Item();
        item.setUserId(user.getId());
        item.setType(Validator.clean(req.getType()));
        item.setItemName(Validator.clean(req.getItemName()));
        item.setCategory(Validator.clean(req.getCategory()));
        item.setDescription(Validator.clean(req.getDescription()));
        item.setLocation(Validator.clean(req.getLocation()));
        item.setItemDate(Validator.clean(req.getItemDate()));

        validate(item);

        itemDAO.create(item);
        item.setReporterName(user.getName());

        List<Match> matches = matchService.findMatches(item);

        Map<String, Object> result = new HashMap<>();
        result.put("item", item);
        result.put("matches", matches);
        return result;
    }

    public Item getById(int id) throws SQLException {
        return itemDAO.findById(id);
    }

    public List<Match> getMatches(Item item) throws SQLException {
        return matchService.findMatches(item);
    }

    public List<Item> getRecent(int limit) throws SQLException {
        return itemDAO.findRecent(limit > 0 ? limit : 6);
    }

    public List<Item> search(String keyword, String type, String category, String location, String status)
            throws SQLException {
        return itemDAO.search(keyword, type, category, location, status);
    }

    public List<Item> getByUser(int userId, String filter) throws SQLException {
        return itemDAO.findByUser(userId, filter);
    }

    public List<Match> getUserActiveMatches(int userId) throws SQLException {
        List<Match> matches = new ArrayList<>();
        List<Item> activeItems = itemDAO.findByUser(userId, "ACTIVE");
        for (Item item : activeItems) {
            List<Match> found = matchService.findMatches(item);
            if (!found.isEmpty()) {
                matches.add(found.get(0)); // best match per report
            }
        }
        return matches;
    }

    public boolean resolve(int id, User user) throws SQLException {
        if (user == null) {
            throw new IllegalStateException("Authentication required.");
        }
        Item item = itemDAO.findById(id);
        if (item == null) {
            throw new IllegalArgumentException("Report not found.");
        }
        if (item.getUserId() != user.getId() && !user.isAdmin()) {
            throw new SecurityException("You can only resolve your own reports.");
        }
        return itemDAO.updateStatus(id, "RESOLVED");
    }

    public boolean delete(int id, User user) throws SQLException {
        if (user == null || !user.isAdmin()) {
            throw new SecurityException("Admin authorization required to delete reports.");
        }
        Item item = itemDAO.findById(id);
        if (item == null) {
            throw new IllegalArgumentException("Report not found.");
        }
        return itemDAO.delete(id);
    }

    public StatsResponse getStats() throws SQLException {
        int lost = itemDAO.countByType("LOST");
        int found = itemDAO.countByType("FOUND");
        int resolved = itemDAO.countByStatus("RESOLVED");
        int users = userDAO.count();
        int total = lost + found;
        return new StatsResponse(lost, found, resolved, users, total);
    }

    public List<Item> getAllItems() throws SQLException {
        return itemDAO.findAll();
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = userDAO.findAll();
        for (User u : users) {
            u.setPassword(null);
        }
        return users;
    }

    private void validate(Item item) {
        if (!Validator.isType(item.getType())) {
            throw new IllegalArgumentException("Please choose whether you lost or found the item.");
        }
        if (Validator.isBlank(item.getItemName()) || Validator.isBlank(item.getLocation())
                || Validator.isBlank(item.getCategory()) || Validator.isBlank(item.getItemDate())) {
            throw new IllegalArgumentException("Item name, category, location and date are required.");
        }
        if (item.getItemName().length() > 100 || item.getLocation().length() > 100) {
            throw new IllegalArgumentException("Item name and location must be under 100 characters.");
        }
        if (item.getDescription() != null && item.getDescription().length() > 1000) {
            throw new IllegalArgumentException("Description must be under 1000 characters.");
        }
        if (!Validator.isCategory(item.getCategory())) {
            throw new IllegalArgumentException("Please choose a valid category.");
        }
        if (!Validator.isValidPastDate(item.getItemDate())) {
            throw new IllegalArgumentException("Please enter a valid date that is not in the future.");
        }
    }
}

