package com.onlineshop.shop.productcatalog.controllers;

import com.onlineshop.shop.productcatalog.dtos.CategoryRequestDto;
import com.onlineshop.shop.productcatalog.dtos.CategoryResponseSelf;
import com.onlineshop.shop.productcatalog.exceptions.CategoryNotPresentException;
import com.onlineshop.shop.productcatalog.exceptions.ProductNotPresentException;
import com.onlineshop.shop.productcatalog.models.Category;
import com.onlineshop.shop.productcatalog.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api_prefix}/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/all")
    public Page<Category> getAllCategories(@RequestParam("pageNo") int pageNo,
                                        @RequestParam("pageSize") int pageSize,
                                        @RequestParam("sortBy") String sortBy){
        return categoryService.getAllCategories(pageNo, pageSize, sortBy);
    }

    @PostMapping("/add")
    public ResponseEntity<CategoryResponseSelf> addCategory(@RequestBody CategoryRequestDto req) {
        Category category =  categoryService.addCategory(req);
        CategoryResponseSelf responseDto = new CategoryResponseSelf(category, "Category added successfully");
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/category/{id}/category")
    public ResponseEntity<CategoryResponseSelf> getSingleCategory(@PathVariable("id") Long id) throws ProductNotPresentException, CategoryNotPresentException {
        Category category = categoryService.getSingleCategory(id);
        return new ResponseEntity<>(new CategoryResponseSelf(category, "Success"),HttpStatus.OK);
    }

    @PutMapping("/category/{id}/update")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody CategoryRequestDto requestDto) throws ProductNotPresentException, CategoryNotPresentException {
        Category category = categoryService.updateCategory(id, requestDto);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @DeleteMapping("/category/{id}/delete")
    public ResponseEntity<CategoryResponseSelf> deleteCategory(@PathVariable Long id) throws ProductNotPresentException, CategoryNotPresentException {
        categoryService.deleteCategory(id);
        CategoryResponseSelf responseDto = new CategoryResponseSelf(null, "Category deleted successfully");
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
