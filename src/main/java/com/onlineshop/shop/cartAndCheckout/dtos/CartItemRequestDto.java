package com.onlineshop.shop.cartAndCheckout.dtos;

import com.onlineshop.shop.product.dtos.ProductDto;

import java.math.BigDecimal;

public class CartItemRequestDto {
    private Long itemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private ProductDto product;
}
