package com.findit.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/** Represents one row of the users table. */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password; // stored as a hash, never exposed to JSON responses

    private String role;
    private String createdAt;

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = "USER";
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public String getFirstName() {
        if (name == null) {
            return "";
        }
        int space = name.indexOf(' ');
        return space > 0 ? name.substring(0, space) : name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
