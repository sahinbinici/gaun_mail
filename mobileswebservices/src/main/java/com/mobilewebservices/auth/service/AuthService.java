package com.mobilewebservices.auth.service;

import com.mobilewebservices.auth.dto.LoginRequest;
import com.mobilewebservices.auth.dto.LoginResponse;
import com.mobilewebservices.security.JwtUtil;

// TEMPORARILY DISABLED - No authentication required
//@Service
public class AuthService {

    /*
    // ALL AUTHENTICATION FUNCTIONALITY DISABLED
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.expiration:86400}")
    private Long jwtExpiration;

    // Hardcoded credentials for demo - In production, use database
    @Value("${app.auth.username:admin}")
    private String adminUsername;

    @Value("${app.auth.password:$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.}") // "password"
    private String adminPassword;

    public AuthService(JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        // Simple authentication - In production, query database
        if (!adminUsername.equals(loginRequest.getUsername())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // For demo purposes, allow both plain text "password" and encoded password
        if (!loginRequest.getPassword().equals("password") &&
            !passwordEncoder.matches(loginRequest.getPassword(), adminPassword)) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(loginRequest.getUsername());

        return new LoginResponse(token, loginRequest.getUsername(), jwtExpiration);
    }

    public boolean validateToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return false;
        }

        String token = authHeader.substring(7);
        return jwtUtil.validateToken(token);
    }

    public String extractUsernameFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);
        return jwtUtil.extractUsername(token);
    }
    */

    // DUMMY METHODS - NO REAL AUTHENTICATION FOR NOW
    private final JwtUtil jwtUtil;

    public AuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        // Always allow login for demo purposes (no real authentication)
        String token = jwtUtil.generateToken(loginRequest.getUsername());
        return new LoginResponse(token, loginRequest.getUsername(), 86400L); // 24 hours
    }

    public boolean validateToken(String authHeader) {
        return true; // Always valid for now (no authentication)
    }

    public String extractUsernameFromToken(String authHeader) {
        return "demo-user"; // Return dummy user
    }
}
