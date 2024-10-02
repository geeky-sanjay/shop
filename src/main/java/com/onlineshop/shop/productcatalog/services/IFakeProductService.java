package com.onlineshop.shop.productcatalog.services;

import com.onlineshop.shop.productcatalog.dtos.ProductRequestDto;
import com.onlineshop.shop.productcatalog.exceptions.ProductNotPresentException;
import com.onlineshop.shop.productcatalog.models.Category;
import com.onlineshop.shop.productcatalog.models.Product;

import java.util.List;

public interface IFakeProductService {
     List<Product> getAllProducts();
     Product getSingleProduct(Long id) throws ProductNotPresentException;
     List<Category> getAllCategories();

     Product addProduct(ProductRequestDto requestDto);
}
