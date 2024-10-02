package com.onlineshop.shop.productcatalog.dtos;

import com.onlineshop.shop.productcatalog.models.Category;
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
