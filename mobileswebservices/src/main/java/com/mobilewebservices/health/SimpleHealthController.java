package com.mobilewebservices.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "Simple health check endpoints")
public class SimpleHealthController {

    private final JdbcTemplate jdbcTemplate;

    public SimpleHealthController(@Qualifier("defaultJdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/status")
    @Operation(summary = "Application status", description = "Check if the application is running")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now().toString());
        status.put("application", "Food Announcements News Event Services");
        status.put("version", "1.0.0");

        return ResponseEntity.ok(status);
    }

    @GetMapping("/database")
    @Operation(summary = "Database health", description = "Check database connectivity")
    public ResponseEntity<Map<String, Object>> getDatabaseHealth() {
        Map<String, Object> health = new HashMap<>();

        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

            if (result != null && result == 1) {
                health.put("status", "UP");
                health.put("database", "Connected");
                health.put("message", "Database connection is healthy");
                return ResponseEntity.ok(health);
            } else {
                health.put("status", "DOWN");
                health.put("database", "Error");
                health.put("message", "Unexpected result from health check query");
                return ResponseEntity.status(503).body(health);
            }
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("database", "Disconnected");
            health.put("message", "Database connection failed: " + e.getMessage());
            return ResponseEntity.status(503).body(health);
        }
    }

    @GetMapping("/info")
    @Operation(summary = "Application info", description = "Get application information")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> info = new HashMap<>();

        // Application details
        info.put("name", "Food Announcements News Event Services");
        info.put("description", "API for managing food programs, announcements, news, and events");
        info.put("version", "1.0.0");
        info.put("startup-time", LocalDateTime.now().toString());

        // Features
        Map<String, Boolean> features = new HashMap<>();
        features.put("jwt-authentication", false); // Temporarily disabled
        features.put("swagger-documentation", true);
        features.put("structured-logging", true);
        features.put("exception-handling", true);
        features.put("input-validation", true);
        features.put("cors-support", true);
        features.put("caching", true);
        features.put("pagination", true);
        info.put("features", features);

        // API endpoints
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("base-path", "/mobilewebservices");
        endpoints.put("swagger-ui", "/mobilewebservices/swagger-ui.html");
        endpoints.put("api-docs", "/mobilewebservices/v3/api-docs");
        endpoints.put("actuator-health", "/mobilewebservices/actuator/health");
        endpoints.put("custom-health", "/mobilewebservices/api/health/status");
        info.put("endpoints", endpoints);

        return ResponseEntity.ok(info);
    }
}
