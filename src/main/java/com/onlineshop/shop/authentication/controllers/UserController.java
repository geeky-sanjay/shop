package com.onlineshop.shop.authentication.controllers;

import com.onlineshop.shop.authentication.dtos.LoginRequestDto;
import com.onlineshop.shop.authentication.dtos.SignupRequestDto;
import com.onlineshop.shop.authentication.exceptions.InvalidCredentialsException;
import com.onlineshop.shop.authentication.models.Token;
import com.onlineshop.shop.authentication.models.User;
import com.onlineshop.shop.authentication.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
@Autowired
UserService userService;

    @PostMapping("/signup")
    public User signUp(@RequestBody SignupRequestDto signupRequestDto){
        String name = signupRequestDto.getName();
        String email = signupRequestDto.getEmail();
        String password = signupRequestDto.getPassword();
        return userService.signUp(name, email, password);
    }

    @PostMapping("/login")
    public ResponseEntity<Token> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            String email = loginRequestDto.getEmail();
            String password = loginRequestDto.getPassword();
            Token token = userService.login(email, password);
            return ResponseEntity.ok(token);
        } catch (InvalidCredentialsException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String token){
        userService.logout(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
