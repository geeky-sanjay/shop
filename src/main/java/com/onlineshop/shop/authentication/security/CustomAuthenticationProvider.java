package com.onlineshop.shop.authentication.security;

import com.onlineshop.shop.authentication.exceptions.InvalidCredentialsException;
import com.onlineshop.shop.authentication.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = (String) authentication.getCredentials();

        // Log the received credentials for debugging
        System.out.println("Authenticating user: " + email);

        try {
            // Use the login method from UserService to validate the email and password
            String token = userService.login(email, password); // JWT token can be generated here

            // Fetch user details to retrieve roles/authorities
            var userDetails = userService.loadUserByUsername(email);

            // If authentication is successful, return an authenticated token
            return new UsernamePasswordAuthenticationToken(
                    userDetails.getUsername(),
                    token,
                    userDetails.getAuthorities()
            );

        } catch (Exception | InvalidCredentialsException ex) {
            throw new BadCredentialsException("Invalid email or password", ex);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
