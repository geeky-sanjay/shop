package com.onlineshop.shop.user.dtos;

import com.onlineshop.shop.user.models.Role;
import com.onlineshop.shop.user.models.User;
import lombok.Data;

import java.util.Collection;

@Data
public class CreateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Collection<Role> userRole;
}
