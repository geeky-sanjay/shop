package com.onlineshop.shop.cartAndCheckout.service;

import com.onlineshop.shop.cartAndCheckout.dtos.CheckoutItemRequestDto;
import com.onlineshop.shop.cartAndCheckout.dtos.StripeResponseDto;
import com.stripe.exception.StripeException;

import java.util.List;

public interface ICheckoutService {
    StripeResponseDto createSession(List<CheckoutItemRequestDto> checkoutItemDtoList) throws StripeException;
}
