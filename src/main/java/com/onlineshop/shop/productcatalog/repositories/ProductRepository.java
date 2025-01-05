package com.onlineshop.shop.productcatalog.repositories;

import com.onlineshop.shop.productcatalog.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findByDescription(String description);
    Product findByName(String name);
    Product findByPrice(Float price);
    Page<Product> findAll(Pageable pageable);
    boolean existsByNameAndBrand(String name, String brand);
}

