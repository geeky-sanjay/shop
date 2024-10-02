package com.onlineshop.shop.notification.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsRequest {
    private String to;
    private String message;
}

