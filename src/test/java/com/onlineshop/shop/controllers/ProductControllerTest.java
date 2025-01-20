package com.onlineshop.shop.controllers;

import com.onlineshop.shop.product.dtos.ProductResponseSelf;
import com.onlineshop.shop.product.exceptions.ProductNotPresentException;
import com.onlineshop.shop.product.models.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class ProductControllerTest {
    @Autowired
    FakeProductController productController;

    @MockBean
    FakeProductService productService;

    @Test
    void getAllProducts() {
        Product p1 = new Product();
                p1.setName("Appo");
        Product p2 = new Product();
                p2.setName("Appo v1");
        Product p3 = new Product();
                p3.setName("Iphone");
        List<Product> products = Arrays.asList(p1,p2,p3);
        Mockito.when(productService.getAllProducts()).thenReturn(products);
        List<Product> actual = productController.getAllProducts();
        Assertions.assertEquals(actual.size(), 2);
    }

    @Test
    public void productNotPresentTest() throws ProductNotPresentException {
        Mockito.when(productService.getSingleProduct(25L)).thenThrow(ProductNotPresentException.class) ;
        ResponseEntity<ProductResponseSelf> actual = productController.getSingleProduct(25L);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }
}