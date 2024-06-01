package com.onlineshop.shop.services;

import com.onlineshop.shop.models.Product;
import org.springframework.data.domain.Page;

public interface IProductJpaService {
    Page<Product> getAllProducts(int pageNo, int pageSize, String sortBy);
}