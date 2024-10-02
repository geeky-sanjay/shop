package com.onlineshop.shop.productcatalog.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequestDto {
    private String title;
    private float price;
    private String description;
    // private String category;
    private Long categoryId;
    private String image;
}

