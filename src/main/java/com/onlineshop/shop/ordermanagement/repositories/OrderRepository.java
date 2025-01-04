package com.onlineshop.shop.ordermanagement.repositories;

import com.onlineshop.shop.ordermanagement.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}
