package com.mobilewebservices.auth.dto;

public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private String username;
    private Long expiresIn; // Seconds

    // Constructors
    public LoginResponse() {}

    public LoginResponse(String token, String tokenType, String username, Long expiresIn) {
        this.token = token;
        this.tokenType = tokenType;
        this.username = username;
        this.expiresIn = expiresIn;
    }

    public LoginResponse(String token, String username, Long expiresIn) {
        this.token = token;
        this.username = username;
        this.expiresIn = expiresIn;
        this.tokenType = "Bearer";
    }

    // Getter and Setter methods
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
}
