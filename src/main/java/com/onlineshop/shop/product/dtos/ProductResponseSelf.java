package com.onlineshop.shop.product.dtos;

import com.onlineshop.shop.product.models.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductResponseSelf {
    private Product product;
    private String message;
}
