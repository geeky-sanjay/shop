package com.onlineshop.shop.ordermanagement.services;

import com.onlineshop.shop.ordermanagement.dtos.OrderDto;
import com.onlineshop.shop.ordermanagement.models.Order;

import java.util.List;

public interface IOrderService {
    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);

    List<OrderDto> getOrdersByUserId(Long userId);

    OrderDto convertToDto(Order order);
}
