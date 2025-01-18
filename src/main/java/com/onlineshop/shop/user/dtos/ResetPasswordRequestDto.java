package com.onlineshop.shop.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequestDto {
    private String email;
    private String newPassword;
    private String confirmPassword;
}
