package com.onlineshop.shop.product.services;

import com.onlineshop.shop.product.dtos.AddProductRequest;
import com.onlineshop.shop.product.dtos.ProductDto;
import com.onlineshop.shop.product.dtos.ProductRequestDto;
import com.onlineshop.shop.product.dtos.ProductUpdateRequest;
import com.onlineshop.shop.product.exceptions.ProductNotPresentException;
import com.onlineshop.shop.product.models.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest product);
    Product getProductById(Long id);
    void deleteProductById(Long id);
    Product updateProduct(ProductUpdateRequest product, Long productId);
    List<Product> getAllProducts();
    List<Product> getProductsByCategory(String category);
    List<Product> getProductsByBrand(String brand);
    List<Product> getProductsByCategoryAndBrand(String category, String brand);
    List<Product> getProductsByName(String name);
    List<Product> getProductsByBrandAndName(String category, String name);
    Long countProductsByBrandAndName(String brand, String name);

    List<ProductDto> getConvertedProducts(List<Product> products);

    ProductDto convertToDto(Product product);
}

