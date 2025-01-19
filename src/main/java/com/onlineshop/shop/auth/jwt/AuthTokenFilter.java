package com.onlineshop.shop.auth.jwt;

import com.onlineshop.shop.auth.user.ShopUserDetailsService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private ShopUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (StringUtils.hasText(jwt) && jwtUtils.validateToken(jwt)) {
                String username = jwtUtils.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);

            }
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage() +" : Invalid or expired token, you may login and try again!");
            return;
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(e.getMessage());
            return;

        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        try {
            String headerAuth = request.getHeader("Authorization");

            // Check if the Authorization header is present and starts with "Bearer "
            if (StringUtils.hasText(headerAuth)) {
                String[] parts = headerAuth.split(" ");

                // Check if there are multiple Bearer tokens
                if (parts.length > 2 || (parts.length == 2 && !parts[0].equals("Bearer"))) {
                    throw new IllegalArgumentException("Invalid Authorization header: multiple Bearer tokens detected.");
                }

                // Ensure the first part is "Bearer"
                if (parts[0].equals("Bearer")) {
                    return parts[1].trim(); // Return the token, trimming any whitespace
                }
            }

            return null; // Return null if no valid token found
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired token");
        }
    }
}