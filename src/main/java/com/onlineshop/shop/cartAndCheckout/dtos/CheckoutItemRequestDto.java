package com.onlineshop.shop.cartAndCheckout.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutItemRequestDto {

    private String productName;
    private int  quantity;
    private double price;
    private long productId;
    private int userId;
    private String currency;
    private long orderId;
}
