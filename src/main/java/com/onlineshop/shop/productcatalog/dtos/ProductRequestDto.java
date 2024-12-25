package com.onlineshop.shop.productcatalog.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequestDto {
    private String title;
    private BigDecimal price;
    private String description;
    // private String category;
    private Long categoryId;
    private String image;
}

