package com.onlineshop.shop.productcatalog.dtos;

import com.onlineshop.shop.productcatalog.models.Product;
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
