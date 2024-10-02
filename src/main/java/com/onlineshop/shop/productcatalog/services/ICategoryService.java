package com.onlineshop.shop.productcatalog.services;

import com.onlineshop.shop.productcatalog.dtos.CategoryRequestDto;
import com.onlineshop.shop.productcatalog.exceptions.CategoryNotPresentException;
import com.onlineshop.shop.productcatalog.exceptions.ProductNotPresentException;
import com.onlineshop.shop.productcatalog.models.Category;
import com.onlineshop.shop.productcatalog.models.Product;
import org.springframework.data.domain.Page;

public interface ICategoryService {
    Category getSingleCategory(Long id) throws CategoryNotPresentException;
    Page<Category> getAllCategories(int pageNo, int pageSize, String sortBy);
    Category addCategory(CategoryRequestDto reqBody);
    Category updateCategory(Long id, CategoryRequestDto requestDto) throws CategoryNotPresentException;
    void deleteCategory(Long id) throws CategoryNotPresentException;
}
