package com.findit.dto;

public class ItemRequest {
    private String type;
    private String itemName;
    private String category;
    private String description;
    private String location;
    private String itemDate;

    public ItemRequest() {}

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
}

