package com.onlineshop.shop.authentication.security.services;

import com.onlineshop.shop.authentication.models.Role;
import org.springframework.security.core.GrantedAuthority;

public class CustomGrantedAuthority implements GrantedAuthority {
    private String role;

    CustomGrantedAuthority(){

    }
    public CustomGrantedAuthority(Role role){
        this.role = role.getName();
    }
    @Override
    public String getAuthority() {
        return role;
    }
}
