package com.onlineshop.shop.cartAndCheckout.exceptions;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(String message) {
        super(message);
    }
}
