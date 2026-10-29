package com.findit.dao;

import com.findit.model.Item;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * All SQL for the items table (Create, Read, Update, Delete).
 * User input is only ever passed through '?' placeholders.
 */
@Repository
public class ItemDAO {

    private static final String SELECT =
            "SELECT i.*, u.name AS reporter_name FROM items i JOIN users u ON u.id = i.user_id ";

    private final DataSource dataSource;

    public ItemDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // ---------- CREATE ----------
    public int create(Item item) throws SQLException {
        String sql = "INSERT INTO items (user_id, type, item_name, category, description, location, item_date, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getUserId());
            ps.setString(2, item.getType());
            ps.setString(3, item.getItemName());
            ps.setString(4, item.getCategory());
            ps.setString(5, item.getDescription());
            ps.setString(6, item.getLocation());
            ps.setString(7, item.getItemDate());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                int id = keys.next() ? keys.getInt(1) : -1;
                item.setId(id);
                item.setStatus("ACTIVE");
                return id;
            }
        }
    }

    // ---------- READ ----------
    public Item findById(int id) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + "WHERE i.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Item> findRecent(int limit) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY i.created_at DESC, i.id DESC LIMIT ?")) {
            ps.setInt(1, limit);
            return list(ps);
        }
    }

    public List<Item> findAll() throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY i.created_at DESC, i.id DESC")) {
            return list(ps);
        }
    }

    public List<Item> findActiveByType(String type) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + "WHERE i.type = ? AND i.status = 'ACTIVE'")) {
            ps.setString(1, type);
            return list(ps);
        }
    }

    /** filter: ALL, LOST, FOUND, ACTIVE or RESOLVED */
    public List<Item> findByUser(int userId, String filter) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT).append("WHERE i.user_id = ? ");
        String value = null;
        if ("LOST".equals(filter) || "FOUND".equals(filter)) {
            sql.append("AND i.type = ? ");
            value = filter;
        } else if ("ACTIVE".equals(filter) || "RESOLVED".equals(filter)) {
            sql.append("AND i.status = ? ");
            value = filter;
        }
        sql.append("ORDER BY i.created_at DESC, i.id DESC");

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            ps.setInt(1, userId);
            if (value != null) {
                ps.setString(2, value);
            }
            return list(ps);
        }
    }

    /**
     * Dynamic search. The SQL text only changes by adding fixed fragments;
     * every user value is bound with a parameter, so SQL injection is impossible.
     */
    public List<Item> search(String keyword, String type, String category, String location, String status)
            throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT).append("WHERE 1 = 1 ");
        List<String> params = new ArrayList<>();

        if (notEmpty(keyword)) {
            sql.append("AND (LOWER(i.item_name) LIKE ? OR LOWER(i.description) LIKE ? "
                    + "OR LOWER(i.category) LIKE ? OR LOWER(i.location) LIKE ?) ");
            String like = "%" + keyword.toLowerCase() + "%";
            for (int k = 0; k < 4; k++) {
                params.add(like);
            }
        }
        if (notEmpty(type)) {
            sql.append("AND i.type = ? ");
            params.add(type);
        }
        if (notEmpty(category)) {
            sql.append("AND i.category = ? ");
            params.add(category);
        }
        if (notEmpty(location)) {
            sql.append("AND LOWER(i.location) LIKE ? ");
            params.add("%" + location.toLowerCase() + "%");
        }
        if (notEmpty(status)) {
            sql.append("AND i.status = ? ");
            params.add(status);
        }
        sql.append("ORDER BY i.created_at DESC, i.id DESC LIMIT 100");

        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int k = 0; k < params.size(); k++) {
                ps.setString(k + 1, params.get(k));
            }
            return list(ps);
        }
    }

    public int countByType(String type) throws SQLException {
        return count("SELECT COUNT(*) FROM items WHERE type = ?", type);
    }

    public int countByStatus(String status) throws SQLException {
        return count("SELECT COUNT(*) FROM items WHERE status = ?", status);
    }

    // ---------- UPDATE ----------
    public boolean updateStatus(int id, String status) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE items SET status = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() == 1;
        }
    }

    // ---------- DELETE ----------
    public boolean delete(int id) throws SQLException {
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM items WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    // ---------- helpers ----------
    private int count(String sql, String value) throws SQLException {
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private List<Item> list(PreparedStatement ps) throws SQLException {
        List<Item> items = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                items.add(map(rs));
            }
        }
        return items;
    }

    private boolean notEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private Item map(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setUserId(rs.getInt("user_id"));
        item.setType(rs.getString("type"));
        item.setItemName(rs.getString("item_name"));
        item.setCategory(rs.getString("category"));
        item.setDescription(rs.getString("description"));
        item.setLocation(rs.getString("location"));
        item.setItemDate(rs.getString("item_date"));
        item.setStatus(rs.getString("status"));
        item.setCreatedAt(rs.getString("created_at"));
        item.setReporterName(rs.getString("reporter_name"));
        return item;
    }
}
