package com.onlineshop.shop.productcatalog.services;

import com.onlineshop.shop.productcatalog.dtos.CategoryRequestDto;
import com.onlineshop.shop.productcatalog.exceptions.CategoryNotPresentException;
import com.onlineshop.shop.productcatalog.models.Category;
import com.onlineshop.shop.productcatalog.models.Product;
import com.onlineshop.shop.productcatalog.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category addCategory(@RequestBody CategoryRequestDto reqBody) {
        Category category = new Category();
        category.setName(reqBody.getName());
        return categoryRepository.save(category);
    }

    @Override
    public Category getSingleCategory(Long id) throws CategoryNotPresentException {
        return categoryRepository.findById(id).orElseThrow(CategoryNotPresentException::new);
    }

    @Override
    public Page<Category> getAllCategories(int pageNo, int pageSize, String sortBy) {
        return categoryRepository.findAll(PageRequest.of(pageNo, pageSize, Sort.by(sortBy)));
    }

    @Override
    public void deleteCategory(Long id) throws CategoryNotPresentException {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
        } else {
            throw new CategoryNotPresentException();
        }
    }

    @Override
    public Category updateCategory(Long id,CategoryRequestDto requestDto) throws CategoryNotPresentException {
        Category category = categoryRepository.findById(id).orElseThrow(CategoryNotPresentException::new);
        category.setName(requestDto.getName());
        return categoryRepository.save(category);
    }
}
