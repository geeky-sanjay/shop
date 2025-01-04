package com.onlineshop.shop.ordermanagement.service;

import com.onlineshop.shop.ordermanagement.models.Order;

import java.util.List;

public interface IOrderService {
    Order placeOrder(Long userId);
    Order getOrder(Long orderId);

    List<Order> getOrdersByUserId(Long userId);
}
