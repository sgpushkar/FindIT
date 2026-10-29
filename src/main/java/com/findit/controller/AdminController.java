package com.findit.controller;

import com.findit.dto.ApiResponse;
import com.findit.dto.StatsResponse;
import com.findit.model.Item;
import com.findit.model.User;
import com.findit.service.AuthService;
import com.findit.service.ItemService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ItemService itemService;
    private final AuthService authService;

    public AdminController(ItemService itemService, AuthService authService) {
        this.itemService = itemService;
        this.authService = authService;
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverview(HttpServletRequest request) {
        User user = authService.getCurrentUser(request);
        if (user == null || !user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required."));
        }

        try {
            StatsResponse stats = itemService.getStats();
            List<Item> items = itemService.getAllItems();
            List<User> users = itemService.getAllUsers();

            Map<String, Object> data = new HashMap<>();
            data.put("stats", stats);
            data.put("items", items);
            data.put("users", users);

            return ResponseEntity.ok(ApiResponse.ok(data));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load admin overview."));
        }
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @PathVariable int id, HttpServletRequest request) {
        User user = authService.getCurrentUser(request);
        if (user == null || !user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required."));
        }

        try {
            boolean deleted = itemService.delete(id, user);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.ok("Report deleted permanently.", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Report no longer exists."));
            }
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Database error while deleting report."));
        }
    }
}

