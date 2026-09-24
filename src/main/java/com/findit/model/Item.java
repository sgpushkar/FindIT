package com.findit.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Represents one lost or found report (a row of the items table). */
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter SHORT = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter LONG = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

    private int id;
    private int userId;
    private String type;        // LOST or FOUND
    private String itemName;
    private String category;
    private String description;
    private String location;
    private String itemDate;    // yyyy-MM-dd
    private String status;      // ACTIVE or RESOLVED
    private String createdAt;
    private String reporterName; // filled by JOIN with users

    public boolean isLost() { return "LOST".equals(type); }
    public boolean isResolved() { return "RESOLVED".equals(status); }

    public String getDisplayDate() { return format(SHORT); }
    public String getLongDate() { return format(LONG); }

    private String format(DateTimeFormatter f) {
        if (itemDate == null || itemDate.isEmpty()) {
            return "-";
        }
        try {
            return LocalDate.parse(itemDate).format(f);
        } catch (Exception e) {
            return itemDate;
        }
    }

    public String getShortDescription() {
        if (description == null || description.isBlank()) {
            return "No description provided.";
        }
        return description.length() > 90 ? description.substring(0, 87).trim() + "..." : description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getItemDate() { return itemDate; }
    public void setItemDate(String itemDate) { this.itemDate = itemDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
}
