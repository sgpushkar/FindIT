package com.findit.controller;

import com.findit.dto.ApiResponse;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final List<String> VALID_FILTERS = Arrays.asList("ALL", "LOST", "FOUND", "ACTIVE", "RESOLVED");

    private final ItemService itemService;
    private final AuthService authService;

    public DashboardController(ItemService itemService, AuthService authService) {
        this.itemService = itemService;
        this.authService = authService;
    }

    @GetMapping("/my-reports")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyReports(
            @RequestParam(required = false, defaultValue = "ALL") String filter,
            HttpServletRequest request) {
        User user = authService.getCurrentUser(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required to view dashboard."));
        }

        String activeFilter = filter.toUpperCase();
        if (!VALID_FILTERS.contains(activeFilter)) {
            activeFilter = "ALL";
        }

        try {
            List<Item> items = itemService.getByUser(user.getId(), activeFilter);
            List<Match> matches = itemService.getUserActiveMatches(user.getId());

            Map<String, Object> data = new HashMap<>();
            data.put("items", items);
            data.put("matches", matches);
            data.put("filter", activeFilter);
            data.put("filters", VALID_FILTERS);

            return ResponseEntity.ok(ApiResponse.ok(data));
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to load your reports."));
        }
    }
}

