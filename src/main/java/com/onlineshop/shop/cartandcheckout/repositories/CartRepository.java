package com.onlineshop.shop.cartandcheckout.repositories;

import com.onlineshop.shop.cartandcheckout.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUserId(Long userId);
}
