package com.onlineshop.shop.user.services;

import com.onlineshop.shop.user.dtos.SignupRequest;
import com.onlineshop.shop.user.dtos.UserDto;
import com.onlineshop.shop.user.dtos.UserUpdateRequest;
import com.onlineshop.shop.user.models.User;

public interface IUserService {
    User getUserById(Long userId);
    User createUser(SignupRequest request);
    User updateUser(UserUpdateRequest request, Long userId);
    void deleteUser(Long userId);

    UserDto convertUserToDto(User user);
}
