package com.onlineshop.shop.product.services;

import com.onlineshop.shop.product.dtos.CategoryRequestDto;
import com.onlineshop.shop.product.exceptions.CategoryNotPresentException;
import com.onlineshop.shop.product.models.Category;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ICategoryService {
    Category getCategoryById(Long id);
    Category getCategoryByName(String name);
    List<Category> getAllCategories();
    Category addCategory(Category category);
    Category updateCategory(Category category, Long id);
    void deleteCategoryById(Long id);
}
