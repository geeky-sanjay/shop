package com.onlineshop.shop.productcatalog.repositories;

import com.onlineshop.shop.productcatalog.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByName(String name);
    boolean existsByName(String name);
}
