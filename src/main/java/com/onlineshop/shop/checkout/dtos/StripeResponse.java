package com.onlineshop.shop.checkout.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StripeResponse {
    private String sessionId;
    private String sessionUrl;
    private String status;
    private String message;

    public StripeResponse(String sessionId) {
        this.sessionId = sessionId;
    }
}
