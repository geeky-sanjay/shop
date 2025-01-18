package com.onlineshop.shop.cartAndCheckout.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StripeResponseDto {
    private String sessionId;
    private String sessionUrl;
    private String status;
    private String message;

    public StripeResponseDto(String sessionId) {
        this.sessionId = sessionId;
    }
}
