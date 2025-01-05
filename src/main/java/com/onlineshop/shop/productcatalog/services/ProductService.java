package com.onlineshop.shop.productcatalog.services;

import com.onlineshop.shop.common.exceptions.ResourceNotFoundException;
import com.onlineshop.shop.productcatalog.dtos.ImageDto;
import com.onlineshop.shop.productcatalog.dtos.ProductDto;
import com.onlineshop.shop.productcatalog.dtos.ProductRequestDto;
import com.onlineshop.shop.productcatalog.models.Category;
import com.onlineshop.shop.productcatalog.models.Image;
import com.onlineshop.shop.productcatalog.models.Product;
import com.onlineshop.shop.productcatalog.repositories.CategoryRepository;
import com.onlineshop.shop.productcatalog.repositories.ImageRepository;
import com.onlineshop.shop.productcatalog.repositories.ProductRepository;
import com.onlineshop.shop.user.exceptions.AlreadyExistException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<Product> getAllProducts(int pageNo, int pageSize, String sortBy) {
        return productRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy)));
    }

    private Product createProduct(ProductRequestDto requestDto)  {
        Product product = new Product();
        product.setName(requestDto.getName());
        product.setBrand(requestDto.getBrand());
        product.setPrice(requestDto.getPrice());
        product.setInventory(requestDto.getInventory());
        product.setDescription(requestDto.getDescription());
        product.setCategory(requestDto.getCategory());
        return product;
    }

    @Override
    public Product addProduct(ProductRequestDto request){
        if(productExists(request.getName(), request.getBrand())){
            throw new AlreadyExistException(request.getBrand() + " " + request.getName() + "already exists, please update the product");
        }
        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(() -> {
                    Category newCategory = new Category(request.getCategory().getName());
                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);

        return productRepository.save(createProduct(request));
    }

    private boolean productExists(String name, String brand) {
        return productRepository.existsByNameAndBrand(name, brand);
    }

    @Override
    public Product updateProduct( Long id, ProductRequestDto requestDto) throws ResourceNotFoundException{
        Product product = productRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Product not found"));
        return productRepository.save(createProduct(requestDto));
    }

    @Override
    public Product productRepository(Long id) {
        return null;
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found!"));
    }

    @Override
    public void deleteProduct(Long id) throws ResourceNotFoundException {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Product not found");
        }
    }


    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos = images.stream()
                .map(image -> modelMapper.map(image, ImageDto.class))
                .toList();
        productDto.setImages(imageDtos);
        return productDto;
    }
}

