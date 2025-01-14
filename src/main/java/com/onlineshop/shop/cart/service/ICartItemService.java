package com.onlineshop.shop.cart.service;

import com.onlineshop.shop.cart.models.CartItem;
import com.onlineshop.shop.product.exceptions.ProductNotPresentException;

public interface ICartItemService {
    void addCartItem(Long cartId, Long productId, int quantity) throws ProductNotPresentException;
    void removeCartItem(Long cartId, Long productId) throws ProductNotPresentException;
    void updateItemQuantity(Long cartId, Long productId, int quantity) throws ProductNotPresentException;

    CartItem getCartItem(Long cartId, Long productId);
}
