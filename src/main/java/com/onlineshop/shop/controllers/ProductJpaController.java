package com.onlineshop.shop.controllers;

import com.onlineshop.shop.models.Product;
import com.onlineshop.shop.services.ProductJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductJpaController {
    @Autowired
    ProductJpaService productJpaService;

    @GetMapping("/jpaProducts")
    public Page<Product> getAllProducts(@RequestParam("pageNo") int pageNo,
                                        @RequestParam("pageSize") int pageSize,
                                        @RequestParam("sortBy") String sortBy){
        return productJpaService.getAllProducts(pageNo, pageSize, sortBy);
    }
}

