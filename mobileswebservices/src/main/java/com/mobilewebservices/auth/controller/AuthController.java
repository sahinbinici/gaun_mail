package com.mobilewebservices.auth.controller;

import com.mobilewebservices.auth.dto.LoginRequest;
import com.mobilewebservices.auth.dto.LoginResponse;
import com.mobilewebservices.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// TEMPORARILY DISABLED - No authentication required
//@RestController
//@RequestMapping("/auth")
//@CrossOrigin(origins = "*")
//@Tag(name = "Authentication", description = "API endpoints for user authentication and authorization")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully authenticated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content),
        @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.authenticate(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate JWT token", description = "Check if the provided JWT token is valid")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token is valid", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "401", description = "Token is invalid or expired", content = @Content(mediaType = "text/plain"))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String token) {
        boolean isValid = authService.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok("Token is valid");
        } else {
            return ResponseEntity.status(401).body("Token is invalid or expired");
        }
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Retrieve current user profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved profile", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "401", description = "Invalid or missing authorization token", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> getProfile(@RequestHeader("Authorization") String token) {
        String username = authService.extractUsernameFromToken(token);
        return ResponseEntity.ok("Profile for user: " + username);
    }
}
