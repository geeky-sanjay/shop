package com.onlineshop.shop.cart.dtos;

import com.onlineshop.shop.product.dtos.ProductDto;

import java.math.BigDecimal;

public class CartItemDto {
    private Long itemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private ProductDto product;
}
