package com.findit.controller;

import com.findit.dto.ApiResponse;
import com.findit.dto.ItemRequest;
import com.findit.dto.StatsResponse;
import com.findit.model.Item;
import com.findit.model.Match;
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
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final AuthService authService;

    public ItemController(ItemService itemService, AuthService authService) {
        this.itemService = itemService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Item>>> search(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false, defaultValue = "") String type,
            @RequestParam(required = false, defaultValue = "") String category,
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false, defaultValue = "") String status) {
        try {
            List<Item> items = itemService.search(q, type, category, location, status);
            return ResponseEntity.ok(ApiResponse.ok(items));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch reports."));
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<Item>>> getRecent(
            @RequestParam(required = false, defaultValue = "6") int limit) {
        try {
            List<Item> items = itemService.getRecent(limit);
            return ResponseEntity.ok(ApiResponse.ok(items));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch recent reports."));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<StatsResponse>> getStats() {
        try {
            StatsResponse stats = itemService.getStats();
            return ResponseEntity.ok(ApiResponse.ok(stats));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch statistics."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getItemDetails(
            @PathVariable int id, HttpServletRequest request) {
        try {
            Item item = itemService.getById(id);
            if (item == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Report not found."));
            }

            User user = authService.getCurrentUser(request);
            boolean isOwner = user != null && user.getId() == item.getUserId();
            boolean canManage = isOwner || (user != null && user.isAdmin());

            List<Match> matches = null;
            if (canManage && !item.isResolved()) {
                matches = itemService.getMatches(item);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("item", item);
            data.put("isOwner", isOwner);
            data.put("canManage", canManage);
            data.put("matches", matches);

            return ResponseEntity.ok(ApiResponse.ok(data));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch report details."));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createItem(
            @RequestBody ItemRequest itemRequest, HttpServletRequest request) {
        User user = authService.getCurrentUser(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("You must be logged in to report an item."));
        }

        try {
            Map<String, Object> created = itemService.create(itemRequest, user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Report submitted successfully.", created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to submit report. Please try again."));
        }
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<Void>> resolveItem(
            @PathVariable int id, HttpServletRequest request) {
        User user = authService.getCurrentUser(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required."));
        }

        try {
            boolean updated = itemService.resolve(id, user);
            if (updated) {
                return ResponseEntity.ok(ApiResponse.ok("Report marked as resolved.", null));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("Could not update report status."));
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Database error while resolving report."));
        }
    }

    @GetMapping("/{id}/matches")
    public ResponseEntity<ApiResponse<List<Match>>> getMatches(@PathVariable int id) {
        try {
            Item item = itemService.getById(id);
            if (item == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Report not found."));
            }
            List<Match> matches = itemService.getMatches(item);
            return ResponseEntity.ok(ApiResponse.ok(matches));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to find matches."));
        }
    }
}

