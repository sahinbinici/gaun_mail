package com.mobilewebservices.security;

// JWT IMPORTS DISABLED
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

// TEMPORARILY DISABLED - No authentication required
//@Component
public class JwtUtil {

    /*
    // ALL JWT FUNCTIONALITY DISABLED
    @Value("${app.jwt.secret:mySecretKey123456789012345678901234567890}")
    private String secret;

    @Value("${app.jwt.expiration:86400}")
    private Long expiration; // 24 hours in seconds

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String username) {
        return createToken(username);
    }

    private String createToken(String subject) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration, ChronoUnit.SECONDS)))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public Boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    */

    // DUMMY METHODS - NO REAL JWT FUNCTIONALITY FOR NOW
    public String generateToken(String username) {
        return "dummy-token-for-" + username;
    }

    public String extractUsername(String token) {
        return "dummy-user";
    }

    public Date extractExpiration(String token) {
        return new Date(System.currentTimeMillis() + 86400000); // 24 hours from now
    }

    public Boolean validateToken(String token) {
        return true; // Always valid for now (no authentication)
    }

    public Boolean validateToken(String token, String username) {
        return true; // Always valid for now (no authentication)
    }

    public Boolean isTokenExpired(String token) {
        return false; // Never expired for now
    }
}
