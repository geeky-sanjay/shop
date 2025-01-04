package com.onlineshop.shop.productcatalog.controllers;

import com.onlineshop.shop.common.exceptions.ResourceNotFoundException;
import com.onlineshop.shop.productcatalog.dtos.ProductRequestDto;
import com.onlineshop.shop.productcatalog.dtos.ProductResponseSelf;
import com.onlineshop.shop.productcatalog.exceptions.ProductNotPresentException;
import com.onlineshop.shop.productcatalog.models.Product;
import com.onlineshop.shop.productcatalog.services.CategoryService;
import com.onlineshop.shop.productcatalog.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    private  final ProductService  productService;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public Page<Product> getAllProducts(@RequestParam("pageNo") int pageNo,
                                        @RequestParam("pageSize") int pageSize,
                                        @RequestParam("sortBy") String sortBy){
        return productService.getAllProducts(pageNo, pageSize, sortBy);
    }
    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody ProductRequestDto requestDto) throws ProductNotPresentException {
        Product product = productService.addProduct(requestDto);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponseSelf> getSingleProduct(@PathVariable("id") Long id) {
        try {
            Product product = productService.getSingleProduct(id);
            return new ResponseEntity<>(new ProductResponseSelf(product, "Success"), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(new ProductResponseSelf(null, ex.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDto requestDto) throws ProductNotPresentException {
       Product product = productService.updateProduct(id, requestDto);
       return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) throws ProductNotPresentException {
          productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully");
    }
}

