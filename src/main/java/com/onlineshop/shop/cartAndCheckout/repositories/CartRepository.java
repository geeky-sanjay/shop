package com.onlineshop.shop.cartAndCheckout.repositories;

import com.onlineshop.shop.cartAndCheckout.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUserId(Long userId);
}
