package com.onlineshop.shop.user.dtos;


import com.onlineshop.shop.cartandcheckout.dtos.CartDto;
import com.onlineshop.shop.ordermanagement.dtos.OrderDto;
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
