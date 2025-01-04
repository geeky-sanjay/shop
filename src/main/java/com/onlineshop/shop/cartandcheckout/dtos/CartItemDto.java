package com.onlineshop.shop.cartandcheckout.dtos;

import com.onlineshop.shop.productcatalog.dtos.ProductDto;

import java.math.BigDecimal;

public class CartItemDto {
    private Long itemId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private ProductDto product;
}
