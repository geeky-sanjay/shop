package com.onlineshop.shop.cartandcheckout.service;

import com.onlineshop.shop.cartandcheckout.models.Cart;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCart(Long cartId);
    void clearCart(Long cartId);
    BigDecimal getTotalPrice(Long cartId);

    Long initializeNewCart();

    Cart getCartByUserId(Long userId);
}
