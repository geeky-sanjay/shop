package com.onlineshop.shop.product.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onlineshop.shop.common.models.BaseModel;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
    public class Category extends BaseModel {
        private String name;
        private String description;

        @JsonIgnore
        @OneToMany(mappedBy = "category")
        private List<Product> products;

        public Category(String name) {
            this.name = name;
        }
    }
