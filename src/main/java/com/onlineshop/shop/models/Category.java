package com.onlineshop.shop.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Category extends BaseModel {
String name;
}
