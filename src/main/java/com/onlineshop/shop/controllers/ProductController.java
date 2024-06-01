package com.onlineshop.shop.controllers;

import com.onlineshop.shop.dtos.ProductRequestDto;
import com.onlineshop.shop.dtos.ProductResponseSelf;
import com.onlineshop.shop.exceptions.ProductNotPresentException;
import com.onlineshop.shop.models.Category;
import com.onlineshop.shop.models.Product;
import com.onlineshop.shop.services.IProductService;
import com.onlineshop.shop.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ProductController {
    @Autowired
    IProductService productService;

    @GetMapping("/products")
    public List<Product> getAllProducts(){
        return productService.getAllProducts().stream().filter((product)->product.getName().startsWith("A")).collect(Collectors.toList());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponseSelf> getSingleProduct(@PathVariable("id") Long id) throws ProductNotPresentException {
        Product product = productService.getSingleProduct(id);
        return new ResponseEntity<>(new ProductResponseSelf(product, "Success"),HttpStatus.OK);
    }

    /*
    @ExceptionHandler(ProductNotPresentException.class)
    public ResponseEntity<ProductResponseSelf> hndleInvalidProduct() {
        ProductResponseSelf productResponseSelf = new ProductResponseSelf(null, "Product not found");
        return new ResponseEntity<>(productResponseSelf, HttpStatus.NOT_FOUND);
    }
    */

    @GetMapping("/products/categories")
    public List<Category> getAllCategories(){
        return productService.getAllCategories();
    }


    @PostMapping("/products")
    public Product addProduct(@RequestBody ProductRequestDto requestDto){
        return new Product();
    }

    @PatchMapping("/products/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody ProductRequestDto requestDto ){
        return new Product();
    }

    @DeleteMapping("/products/{id}")
    public boolean deleteProduct(@PathVariable Long id){
        return true;
    }



    @GetMapping("/products/category/{id}")
    public List<Product> getAllProductsInCategory(@PathVariable("id") Long id){
        return new ArrayList<>();
    }
}
