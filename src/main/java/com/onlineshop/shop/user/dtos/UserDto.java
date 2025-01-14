package com.onlineshop.shop.user.dtos;


import com.onlineshop.shop.cart.dtos.CartDto;
import com.onlineshop.shop.order.dtos.OrderDto;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private List<OrderDto> orders;
    private CartDto cart;
}
