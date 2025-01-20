package com.onlineshop.shop.product.dtos;

import com.onlineshop.shop.product.models.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryResponseSelf {
    private Category category;
    private String message;
}
