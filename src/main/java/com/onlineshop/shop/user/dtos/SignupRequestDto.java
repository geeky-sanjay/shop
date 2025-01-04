package com.onlineshop.shop.user.dtos;

import lombok.Data;

@Data
public class SignupRequestDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
