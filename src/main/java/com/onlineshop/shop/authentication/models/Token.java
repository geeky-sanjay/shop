package com.onlineshop.shop.authentication.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Token extends BaseModel{
    private String value;
    private boolean isDeleted;
    private Date expirydate;
    @ManyToOne
    private User user;


}
