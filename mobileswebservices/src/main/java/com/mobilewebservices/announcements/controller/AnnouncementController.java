package com.mobilewebservices.announcements.controller;

import com.mobilewebservices.announcements.dto.AnnouncementDto;
import com.mobilewebservices.announcements.service.AnnouncementService;
import com.mobilewebservices.common.dto.PageRequest;
import com.mobilewebservices.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@Tag(name = "Announcements", description = "API endpoints for managing announcements")
// @SecurityRequirement(name = "Bearer Authentication") // Disabled - no authentication
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping
    @Operation(summary = "Get all announcements", description = "Retrieve all announcements from the database")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved announcements",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnnouncementDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<AnnouncementDto>> getAllAnnouncements() {
        return ResponseEntity.ok(announcementService.getAllAnnouncements());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get announcement by ID", description = "Retrieve a specific announcement by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved announcement",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnnouncementDto.class))),
        @ApiResponse(responseCode = "404", description = "Announcement not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required", content = @Content)
    })
    public ResponseEntity<AnnouncementDto> getAnnouncementById(
            @Parameter(description = "ID of the announcement to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(announcementService.getAnnouncementById(id));
    }

    @GetMapping("/section/{sectionId}")
    @Operation(summary = "Get announcements by section", description = "Retrieve all announcements for a specific section")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved announcements by section",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnnouncementDto.class)))
    })
    public ResponseEntity<List<AnnouncementDto>> getAnnouncementsBySectionId(
            @Parameter(description = "ID of the section") @PathVariable Long sectionId) {
        return ResponseEntity.ok(announcementService.getAnnouncementsBySectionId(sectionId));
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Get announcements by author", description = "Retrieve all announcements created by a specific author")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved announcements by author",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnnouncementDto.class)))
    })
    public ResponseEntity<List<AnnouncementDto>> getAnnouncementsByAuthorId(
            @Parameter(description = "ID of the author") @PathVariable Long authorId) {
        return ResponseEntity.ok(announcementService.getAnnouncementsByAuthorId(authorId));
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest announcements", description = "Retrieve the 10 most recent announcements")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved latest announcements",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnnouncementDto.class)))
    })
    public ResponseEntity<List<AnnouncementDto>> getLatest10Announcements() {
        return ResponseEntity.ok(announcementService.getLatest10Announcements());
    }

    @GetMapping("/last-month")
    @Operation(summary = "Get last month announcements", description = "Retrieve the 30 most recent announcements")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved latest 30 announcements",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AnnouncementDto.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    public ResponseEntity<List<AnnouncementDto>> getAnnouncementsFromLastMonth() {
        return ResponseEntity.ok(announcementService.getAnnouncementsFromLastMonth());
    }

    @PostMapping("/paginated")
    @Operation(summary = "Get announcements with pagination", description = "Retrieve announcements with pagination support")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated announcements",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid pagination parameters", content = @Content)
    })
    public ResponseEntity<PageResponse<AnnouncementDto>> getAllAnnouncementsPaginated(
            @Valid @RequestBody PageRequest pageRequest) {
        return ResponseEntity.ok(announcementService.getAllAnnouncementsPaginated(pageRequest));
    }

    @PostMapping("/section/{sectionId}/paginated")
    @Operation(summary = "Get announcements by section with pagination", description = "Retrieve announcements for a specific section with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated announcements by section",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)))
    })
    public ResponseEntity<PageResponse<AnnouncementDto>> getAnnouncementsBySectionIdPaginated(
            @Parameter(description = "ID of the section") @PathVariable Long sectionId,
            @Valid @RequestBody PageRequest pageRequest) {
        return ResponseEntity.ok(announcementService.getAnnouncementsBySectionIdPaginated(sectionId, pageRequest));
    }

    @GetMapping("/paginated-query")
    @Operation(summary = "Get announcements with query parameters pagination", description = "Retrieve announcements with pagination using query parameters")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated announcements",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponse.class)))
    })
    public ResponseEntity<PageResponse<AnnouncementDto>> getAllAnnouncementsPaginatedQuery(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "ASC") String sortDirection) {

        PageRequest pageRequest = new PageRequest(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(announcementService.getAllAnnouncementsPaginated(pageRequest));
    }
}
