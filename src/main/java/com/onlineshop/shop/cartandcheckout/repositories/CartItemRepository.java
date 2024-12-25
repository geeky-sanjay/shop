package com.onlineshop.shop.cartandcheckout.repositories;

import com.onlineshop.shop.cartandcheckout.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    void deleteAllByCartId(Long cartId);
}
