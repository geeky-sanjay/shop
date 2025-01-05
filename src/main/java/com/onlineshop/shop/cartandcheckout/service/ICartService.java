package com.onlineshop.shop.cartandcheckout.service;

import com.onlineshop.shop.cartandcheckout.models.Cart;
import com.onlineshop.shop.user.models.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCart(Long cartId);
    void clearCart(Long cartId);
    BigDecimal getTotalPrice(Long cartId);
    Cart initializeNewCart(User user);
    Cart getCartByUserId(Long userId);
}
