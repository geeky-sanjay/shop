package com.onlineshop.shop.services;

import com.onlineshop.shop.dtos.ProductRequestDto;
import com.onlineshop.shop.exceptions.ProductNotPresentException;
import com.onlineshop.shop.models.Category;
import com.onlineshop.shop.models.Product;

import java.util.List;

public interface IProductService {
     List<Product> getAllProducts();
     Product getSingleProduct(Long id) throws ProductNotPresentException;
     List<Category> getAllCategories();

     Product addProduct(ProductRequestDto requestDto);
}
