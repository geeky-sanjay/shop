package com.onlineshop.shop.productcatalog.exceptions;

public class ProductNotPresentException extends  Throwable{
    public ProductNotPresentException(String message) {
        super(message);
    }
}
