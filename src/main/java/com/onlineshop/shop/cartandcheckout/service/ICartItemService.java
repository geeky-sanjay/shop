package com.onlineshop.shop.cartandcheckout.service;

import com.onlineshop.shop.cartandcheckout.models.Cart;
import com.onlineshop.shop.cartandcheckout.models.CartItem;
import com.onlineshop.shop.productcatalog.exceptions.ProductNotPresentException;

import java.math.BigDecimal;

public interface ICartItemService {
    void addCartItem(Long cartId, Long productId, int quantity) throws ProductNotPresentException;
    void removeCartItem(Long cartId, Long productId) throws ProductNotPresentException;
    void updateItemQuantity(Long cartId, Long productId, int quantity) throws ProductNotPresentException;

    CartItem getCartItem(Long cartId, Long productId);
}
