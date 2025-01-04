package com.onlineshop.shop.productcatalog.services;

import com.onlineshop.shop.common.exceptions.ResourceNotFoundException;
import com.onlineshop.shop.productcatalog.dtos.CategoryRequestDto;
import com.onlineshop.shop.productcatalog.dtos.ProductRequestDto;
import com.onlineshop.shop.productcatalog.exceptions.CategoryNotPresentException;
import com.onlineshop.shop.productcatalog.models.Category;
import com.onlineshop.shop.productcatalog.models.Product;
import com.onlineshop.shop.productcatalog.repositories.CategoryRepository;
import com.onlineshop.shop.productcatalog.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Page<Product> getAllProducts(int pageNo, int pageSize, String sortBy) {
        return productRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy)));
    }
    private Product setProduct(Product product, ProductRequestDto requestDto) throws ResourceNotFoundException {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(()->new ResourceNotFoundException("Product not found"));
        product.setName(requestDto.getTitle());
        product.setPrice(requestDto.getPrice());
        product.setDescription(requestDto.getDescription());
        // product.setImages(requestDto.getImage());
        product.setCategory(category);
        return product;
    }
    @Override
    public Product addProduct(ProductRequestDto reqBody) throws  ResourceNotFoundException{
        Product product = new Product();
        return productRepository.save(setProduct(product, reqBody));
    }

    @Override
    public Product updateProduct( Long id, ProductRequestDto requestDto) throws ResourceNotFoundException{
        Product product = productRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Product not found"));
        return productRepository.save(setProduct(product, requestDto));
    }

    @Override
    public Product getSingleProduct(Long id) throws ResourceNotFoundException {
        return productRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Product not found"));
    }

    @Override
    public void deleteProduct(Long id) throws ResourceNotFoundException {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Product not found");
        }
    }



}

