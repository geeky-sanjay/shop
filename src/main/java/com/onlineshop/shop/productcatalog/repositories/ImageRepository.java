package com.onlineshop.shop.productcatalog.repositories;

import com.onlineshop.shop.productcatalog.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByProductId(Long id);
}
