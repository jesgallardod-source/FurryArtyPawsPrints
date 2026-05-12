package com.FurryArtyPawPrints.ViewModule.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtUtility {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Generate JWT token with user claims
     * @param userEmail User's email
     * @param userId User's ID
     * @return Generated JWT token string
     */
    public String generateToken(String userEmail, Integer userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", userEmail);
        return createToken(claims, userEmail);
    }

    /**
     * Create JWT token with claims and subject
     * @param claims Token claims (userId, email, roles, etc.)
     * @param subject Token subject (typically user email)
     * @return JWT token string
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract user email from token
     * @param token JWT token
     * @return User email
     * @throws JwtException if token is invalid
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extract userId from token
     * @param token JWT token
     * @return User ID
     * @throws JwtException if token is invalid
     */
    public Integer extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        Object userIdObj = claims.get("userId");
        if (userIdObj instanceof Integer) {
            return (Integer) userIdObj;
        } else if (userIdObj instanceof Long) {
            return ((Long) userIdObj).intValue();
        }
        return null;
    }

    /**
     * Extract expiration date from token
     * @param token JWT token
     * @return Token expiration date
     * @throws JwtException if token is invalid
     */
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * Extract all claims from token
     * @param token JWT token
     * @return Claims object containing all token data
     * @throws JwtException if token is invalid or expired
     */
    private Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if token is expired
     * @param token JWT token
     * @return true if token is expired, false otherwise
     */
    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            log.warn("Token is expired: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Validate token - checks signature, expiration, and format
     * @param token JWT token
     * @return true if token is valid, false otherwise
     */
    public Boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            if (isTokenExpired(token)) {
                log.warn("Token has expired");
                return false;
            }

            log.debug("Token is valid for user: {}", extractEmail(token));
            return true;

        } catch (ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT: {}", e.getMessage());
            return false;
        } catch (SignatureException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error during token validation: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate token and extract user email
     * @param token JWT token
     * @return User email if valid, null otherwise
     */
    public String validateTokenAndGetEmail(String token) {
        if (validateToken(token)) {
            return extractEmail(token);
        }
        return null;
    }

    /**
     * Validate token and extract user ID
     * @param token JWT token
     * @return User ID if valid, null otherwise
     */
    public Integer validateTokenAndGetUserId(String token) {
        if (validateToken(token)) {
            return extractUserId(token);
        }
        return null;
    }

    /**
     * Extract Bearer token from Authorization header
     * @param bearerToken Authorization header value (e.g., "Bearer <token>")
     * @return JWT token without "Bearer " prefix
     */
    public String extractTokenFromBearerString(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Get remaining time until token expiration in milliseconds
     * @param token JWT token
     * @return Remaining time in milliseconds, -1 if expired
     */
    public long getExpirationTimeRemaining(String token) {
        try {
            Date expirationDate = extractExpiration(token);
            long remaining = expirationDate.getTime() - new Date().getTime();
            return remaining > 0 ? remaining : -1;
        } catch (Exception e) {
            log.error("Error getting expiration time: {}", e.getMessage());
            return -1;
        }
    }
}
