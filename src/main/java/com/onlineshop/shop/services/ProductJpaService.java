package com.onlineshop.shop.services;

import com.onlineshop.shop.models.Product;
import com.onlineshop.shop.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductJpaService implements IProductJpaService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Product> getAllProducts(int pageNo, int pageSize, String sortBy) {
        return productRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy)));
    }
}

