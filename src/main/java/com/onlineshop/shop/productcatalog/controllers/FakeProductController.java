package com.onlineshop.shop.productcatalog.controllers;

import com.onlineshop.shop.productcatalog.dtos.ProductRequestDto;
import com.onlineshop.shop.productcatalog.dtos.ProductResponseSelf;
import com.onlineshop.shop.productcatalog.exceptions.ProductNotPresentException;
import com.onlineshop.shop.productcatalog.models.Category;
import com.onlineshop.shop.productcatalog.models.Product;
import com.onlineshop.shop.productcatalog.services.IFakeProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FakeProductController {
    @Autowired
    IFakeProductService productService;

    @GetMapping("/fake-products")
    public List<Product> getAllProducts(){
        return productService.getAllProducts().stream().filter((product)->product.getName().startsWith("A")).collect(Collectors.toList());
    }

    @GetMapping("/fake-products/{id}")
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

    @GetMapping("/fake-products/categories")
    public List<Category> getAllCategories(){
        return productService.getAllCategories();
    }


    @PostMapping("/fake-products")
    public Product addProduct(@RequestBody ProductRequestDto requestDto){
        return new Product();
    }

    @PatchMapping("/fake-products/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody ProductRequestDto requestDto ){
        return new Product();
    }

    @DeleteMapping("/fake-products/{id}")
    public boolean deleteProduct(@PathVariable Long id){
        return true;
    }



    @GetMapping("/products/category/{id}")
    public List<Product> getAllProductsInCategory(@PathVariable("id") Long id){
        return new ArrayList<>();
    }
}
