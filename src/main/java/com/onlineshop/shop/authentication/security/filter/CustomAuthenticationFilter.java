package com.onlineshop.shop.authentication.security.filter;

import com.onlineshop.shop.authentication.exceptions.InvalidCredentialsException;
import com.onlineshop.shop.authentication.models.User;
import com.onlineshop.shop.authentication.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public CustomAuthenticationFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager, UserService userService, ObjectMapper objectMapper) {
        super(defaultFilterProcessesUrl);
        setAuthenticationManager(authenticationManager);
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        // Parse the JSON request to extract email and password
        Map<String, String> credentials = objectMapper.readValue(request.getInputStream(), Map.class);
        String email = credentials.get("email");
        String password = credentials.get("password");

        // Log received credentials for debugging
        System.out.println("Attempting authentication for email: " + email);

        // Create the authentication token
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);
        System.out.println("Attempting authentication for authRequest: " + authRequest.getCredentials());

        // Perform authentication
        Authentication auth = getAuthenticationManager().authenticate(authRequest);
        System.out.println("Authentication successful for user: " + auth.getName());
        return auth;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        // Generate JWT token using UserService
        String email = authResult.getName();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) authResult.getAuthorities();
        String token = null;
        System.out.println("auth before service" + authResult.getCredentials());
        try {
            // User user = userService.findByEmail(email); // Fetch user from the database
            token = userService.login(email, (String) authResult.getCredentials()); // Call login method
        } catch (InvalidCredentialsException e) {
            throw new RuntimeException("Error during login: " + e.getMessage(), e);
        }

        // Return the token in the response body
        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("token", token);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        // Return error response
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid email or password");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    // Optional: Add a method to get authorities if needed
    private List<GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}
