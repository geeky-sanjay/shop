package com.onlineshop.shop.user.service;

import com.onlineshop.shop.user.dtos.CreateUserRequest;
import com.onlineshop.shop.user.models.User;

public interface IUserService {
    User getUserById(Long userId);
    User createUser(CreateUserRequest user);
}
