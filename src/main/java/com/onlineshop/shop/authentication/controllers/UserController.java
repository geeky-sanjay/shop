package com.onlineshop.shop.authentication.controllers;

import com.onlineshop.shop.authentication.dtos.LoginRequestDto;
import com.onlineshop.shop.authentication.dtos.SignupRequestDto;
import com.onlineshop.shop.authentication.exceptions.InvalidCredentialsException;
import com.onlineshop.shop.authentication.models.User;
import com.onlineshop.shop.authentication.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Handles user registration.
     *
     * @param signupRequestDto The signup request payload.
     * @return The created User object.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignupRequestDto signupRequestDto){
        String name = signupRequestDto.getName();
        String email = signupRequestDto.getEmail();
        String password = signupRequestDto.getPassword();
        User user = userService.signUp(name, email, password);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    // The endpoint for login
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto credentials) {
        try {
            String token = userService.login(credentials.getEmail(), credentials.getPassword());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    /**
     * Handles user logout.
     *
     * @param token The JWT token to invalidate.
     * @return A ResponseEntity with appropriate HTTP status.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String token){
        userService.logout(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
