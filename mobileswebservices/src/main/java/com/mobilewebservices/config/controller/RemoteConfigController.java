package com.mobilewebservices.config.controller;

import com.mobilewebservices.config.dto.ConfigVersionDto;
import com.mobilewebservices.config.dto.UsefulLinkDto;
import com.mobilewebservices.config.service.RemoteConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/useful/config")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Remote Config", description = "Mobile app remote configuration endpoints")
public class RemoteConfigController {

    private final RemoteConfigService remoteConfigService;

    /**
     * Get all active useful links
     * Used by mobile app to fetch dynamic links
     */
    @GetMapping("/useful-links")
    @Operation(summary = "Get active useful links", description = "Returns all active useful links for mobile app")
    public ResponseEntity<List<UsefulLinkDto>> getActiveUsefulLinks() {
        log.info("GET /mobilewebservices/config/useful-links - Fetching active useful links");

        try {
            List<UsefulLinkDto> links = remoteConfigService.getActiveUsefulLinks();
            log.info("Successfully fetched {} active useful links", links.size());
            return ResponseEntity.ok(links);
        } catch (Exception e) {
            log.error("Error fetching active useful links", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Legacy endpoint with underscore for backward compatibility
     * Redirects to the standard endpoint
     */
    @GetMapping("/useful_links")
    @Operation(summary = "Get active useful links (legacy)", description = "Legacy endpoint - use /useful-links instead")
    public ResponseEntity<List<UsefulLinkDto>> getActiveUsefulLinksLegacy() {
        log.info("GET /mobilewebservices/config/useful_links - Legacy endpoint called, redirecting to standard endpoint");
        return getActiveUsefulLinks();
    }

    /**
     * Get all useful links (including inactive) - Admin endpoint
     */
    @GetMapping("/useful-links/all")
    @Operation(summary = "Get all useful links", description = "Returns all useful links including inactive ones (admin)")
    public ResponseEntity<List<UsefulLinkDto>> getAllUsefulLinks() {
        log.info("GET /mobilewebservices/config/useful-links/all - Fetching all useful links");

        try {
            List<UsefulLinkDto> links = remoteConfigService.getAllUsefulLinks();
            log.info("Successfully fetched {} useful links", links.size());
            return ResponseEntity.ok(links);
        } catch (Exception e) {
            log.error("Error fetching all useful links", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get configuration version
     * Used by mobile app to check for updates
     */
    @GetMapping("/version")
    @Operation(summary = "Get config version", description = "Returns current configuration version")
    public ResponseEntity<ConfigVersionDto> getConfigVersion() {
        log.info("GET /mobilewebservices/config/version - Fetching config version");

        try {
            ConfigVersionDto version = remoteConfigService.getConfigVersion();
            log.info("Successfully fetched config version: {}", version.getVersion());
            return ResponseEntity.ok(version);
        } catch (Exception e) {
            log.error("Error fetching config version", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create a new useful link - Admin endpoint
     */
    @PostMapping("/useful-links")
    @Operation(summary = "Create useful link", description = "Creates a new useful link (admin)")
    public ResponseEntity<UsefulLinkDto> createUsefulLink(@Valid @RequestBody UsefulLinkDto dto) {
        log.info("POST /mobilewebservices/config/useful-links - Creating new useful link: {}", dto.getTitle());

        try {
            UsefulLinkDto created = remoteConfigService.createUsefulLink(dto);
            log.info("Successfully created useful link with ID: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error creating useful link", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update an existing useful link - Admin endpoint
     */
    @PutMapping("/useful-links/{id}")
    @Operation(summary = "Update useful link", description = "Updates an existing useful link (admin)")
    public ResponseEntity<UsefulLinkDto> updateUsefulLink(
            @PathVariable String id,
            @Valid @RequestBody UsefulLinkDto dto) {
        log.info("PUT /mobilewebservices/config/useful-links/{} - Updating useful link", id);

        try {
            UsefulLinkDto updated = remoteConfigService.updateUsefulLink(id, dto);
            log.info("Successfully updated useful link with ID: {}", id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating useful link", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete a useful link - Admin endpoint
     */
    @DeleteMapping("/useful-links/{id}")
    @Operation(summary = "Delete useful link", description = "Deletes a useful link (admin)")
    public ResponseEntity<Void> deleteUsefulLink(@PathVariable String id) {
        log.info("DELETE /mobilewebservices/config/useful-links/{} - Deleting useful link", id);

        try {
            remoteConfigService.deleteUsefulLink(id);
            log.info("Successfully deleted useful link with ID: {}", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting useful link", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Toggle link active status - Admin endpoint
     */
    @PatchMapping("/useful-links/{id}/toggle")
    @Operation(summary = "Toggle link status", description = "Toggles active/inactive status of a link (admin)")
    public ResponseEntity<UsefulLinkDto> toggleLinkStatus(@PathVariable String id) {
        log.info("PATCH /mobilewebservices/config/useful-links/{}/toggle - Toggling link status", id);

        try {
            UsefulLinkDto updated = remoteConfigService.toggleLinkStatus(id);
            log.info("Successfully toggled status for link with ID: {}", id);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error toggling link status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Clear all caches - Admin endpoint
     */
    @PostMapping("/cache/clear")
    @Operation(summary = "Clear caches", description = "Clears all remote config caches (admin)")
    public ResponseEntity<String> clearCaches() {
        log.info("POST /mobilewebservices/config/cache/clear - Clearing all caches");

        try {
            remoteConfigService.clearAllCaches();
            log.info("Successfully cleared all caches");
            return ResponseEntity.ok("All caches cleared successfully");
        } catch (Exception e) {
            log.error("Error clearing caches", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error clearing caches: " + e.getMessage());
        }
    }
}
