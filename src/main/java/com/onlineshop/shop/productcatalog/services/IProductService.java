package com.onlineshop.shop.productcatalog.services;

import com.onlineshop.shop.productcatalog.dtos.CategoryRequestDto;
import com.onlineshop.shop.productcatalog.dtos.ProductRequestDto;
import com.onlineshop.shop.productcatalog.exceptions.CategoryNotPresentException;
import com.onlineshop.shop.productcatalog.exceptions.ProductNotPresentException;
import com.onlineshop.shop.productcatalog.models.Category;
import com.onlineshop.shop.productcatalog.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;

public interface IProductService {
   //Products
    Page<Product> getAllProducts(int pageNo, int pageSize, String sortBy);
    Product addProduct( ProductRequestDto reqBody) throws ProductNotPresentException;
    Product updateProduct( Long id, ProductRequestDto requestDto) throws ProductNotPresentException;
    Product getSingleProduct(Long id) throws ProductNotPresentException;
    void deleteProduct(Long id) throws  ProductNotPresentException;
}

