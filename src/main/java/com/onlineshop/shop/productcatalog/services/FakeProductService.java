package com.onlineshop.shop.productcatalog.services;

import com.onlineshop.shop.common.exceptions.ResourceNotFoundException;
import com.onlineshop.shop.productcatalog.dtos.ProductRequestDto;
import com.onlineshop.shop.productcatalog.dtos.ProductResponseDto;
import com.onlineshop.shop.productcatalog.exceptions.ProductNotPresentException;
import com.onlineshop.shop.productcatalog.models.Category;
import com.onlineshop.shop.productcatalog.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class FakeProductService implements IFakeProductService {
    @Autowired
    RestTemplate restTemplate;
    String url = "https://fakestoreapi.com";

    @Override
    public Product getSingleProduct(Long id) throws ProductNotPresentException {
        if(id>20 && id<=40){
            throw new ResourceNotFoundException("Product not found");
        }
        if(id>40){
            throw new ArithmeticException();
        }
        ProductResponseDto responseDto = restTemplate.getForObject(url+"/products/"+id, ProductResponseDto.class);
        return getProductFromResponseDTO(responseDto);
    }

    @Override
    public List<Product> getAllProducts(){

       ProductResponseDto[] products = restTemplate.getForObject(url+"/products", ProductResponseDto[].class);
       List<Product> responseDtoList = new ArrayList<>();
       if(products != null) {
           for (ProductResponseDto responseDto : products) {
               responseDtoList.add(getProductFromResponseDTO(responseDto));
           }
       }
       return responseDtoList;
    }




    @Override
    public List<Category> getAllCategories() {
        long id = 0L;
        String[] categoryListResponse = restTemplate.getForObject(url + "/products/categories", String[].class);
        List<Category> responseDtoList = new ArrayList<>();
        for (String responseDto : categoryListResponse) {
            id = id+1;
            responseDtoList.add(getCategoryFromResponse(responseDto, id));
        }
        return responseDtoList;
    }

    public Product addProduct(ProductRequestDto requestDto){
        return null;
    }

    private Category getCategoryFromResponse(String categoryResponse, Long id){
        Category category = new Category();
        category.setName(categoryResponse);
        category.setId(id);
        return category;
    }
    private Product getProductFromResponseDTO(ProductResponseDto responseDto) {
        Product product = new Product();
        product.setId(responseDto.getId());
        product.setName(responseDto.getTitle());
        //product.setImage(responseDto.getImage());
        product.setPrice(responseDto.getPrice());
        product.setDescription(responseDto.getDescription());
        product.setCategory(new Category());
        product.getCategory().setName(responseDto.getCategory());
        return product;
    }
}
