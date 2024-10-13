package com.onlineshop.shop.authentication.security;

import com.onlineshop.shop.authentication.models.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${JWT_EXPIRATION_MS}")
    private long jwtExpirationMs;

    private Key getSigningKey() {
        return new SecretKeySpec(jwtSecret.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }

    /**
     * Generates a JWT token for the authenticated user.
     *
     * @param user The authenticated user.
     * @return The generated JWT token.
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // Add roles as a claim
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .addClaims(claims)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Validates the JWT token.
     *
     * @param token The JWT token to validate.
     * @return true if valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // Log the exception as needed
            System.err.println("Invalid JWT token: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Extracts Authentication information from the JWT token.
     *
     * @param token The JWT token.
     * @return An Authentication object containing user details and authorities.
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.getSubject();

        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }
}
