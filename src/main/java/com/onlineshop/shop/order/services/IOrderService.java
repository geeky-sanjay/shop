package com.onlineshop.shop.order.services;

import com.onlineshop.shop.order.dtos.OrderDto;
import com.onlineshop.shop.order.models.Order;

import java.util.List;

public interface IOrderService {
    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);

    List<OrderDto> getOrdersByUserId(Long userId);

    OrderDto convertToDto(Order order);
}
