package com.onlineshop.shop.authentication.security;

import com.onlineshop.shop.authentication.models.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private final SecretKey signInKey;
    private final long expirationTime = 3600000;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    public JwtTokenProvider() {
        // Generate a secure key for HMAC-SHA256
        // this.signInKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String encodedKey = Encoders.BASE64.encode(secretKey.getEncoded());
        System.out.println("keys..." + encodedKey);
        this.signInKey = secretKey;
    }
    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${JWT_EXPIRATION_MS}")
    private long jwtExpirationMs;

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token for the authenticated user.
     *
     * @param username The authenticated user.
     * @return The generated JWT token.
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(signInKey)
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
                    .setSigningKey(signInKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signInKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Extracts Authentication information from the JWT token.
     *
     * @param token The JWT token.
     * @return An Authentication object containing user details and authorities.
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.getSubject();

        // Extract roles from claims and add "ROLE_" prefix if needed
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(email, null, authorities);
    }
}
