package com.onlineshop.shop.productcatalog.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Product extends BaseModel {
    private String description;
    private String image;
    private BigDecimal price;

    @ManyToOne
    private Category category;
}
