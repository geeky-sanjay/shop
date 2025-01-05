package com.onlineshop.shop.productcatalog.controllers;

import com.onlineshop.shop.common.dtos.ApiResponse;
import com.onlineshop.shop.common.exceptions.ResourceNotFoundException;
import com.onlineshop.shop.productcatalog.dtos.ProductDto;
import com.onlineshop.shop.productcatalog.dtos.ProductRequestDto;
import com.onlineshop.shop.productcatalog.dtos.ProductResponseSelf;
import com.onlineshop.shop.productcatalog.exceptions.ProductNotPresentException;
import com.onlineshop.shop.productcatalog.models.Product;
import com.onlineshop.shop.productcatalog.services.CategoryService;
import com.onlineshop.shop.productcatalog.services.ProductService;
import com.onlineshop.shop.user.exceptions.AlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${api_prefix}/products")
public class ProductController {

    private  final ProductService  productService;

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
    }

    @GetMapping("/all")
    public Page<Product> getAllProducts(@RequestParam("pageNo") int pageNo,
                                        @RequestParam("pageSize") int pageSize,
                                        @RequestParam("sortBy") String sortBy){
        return productService.getAllProducts(pageNo, pageSize, sortBy);
    }

    @GetMapping("product/{productId}/product")
    public ResponseEntity<ProductResponseSelf> getProductById(@PathVariable Long productId) {
        try {
            Product product = productService.getProductById(productId);
            return new ResponseEntity<>(new ProductResponseSelf(product, "Success"), OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(new ProductResponseSelf(null, ex.getMessage()), NOT_FOUND);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody ProductRequestDto requestDto) throws ProductNotPresentException {
        try {
            Product product = productService.addProduct(requestDto);
            ProductDto productDto = productService.convertToDto(product);

            return ResponseEntity.ok(new ApiResponse("Product added successfully", productDto));
        } catch (AlreadyExistException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/product/{productId}/update")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId, @RequestBody ProductRequestDto requestDto) throws ProductNotPresentException {
       Product product = productService.updateProduct(productId, requestDto);
       return new ResponseEntity<>(product, OK);
    }

    @DeleteMapping("/product/{productId}/delete")
    public ResponseEntity<?> deleteProduct(@PathVariable Long productId) throws ProductNotPresentException {
          productService.deleteProduct(productId);
        return ResponseEntity.ok("Product deleted successfully");
    }
}

