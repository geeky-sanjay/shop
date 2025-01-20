package com.onlineshop.shop.cartAndCheckout.models;

import com.onlineshop.shop.common.models.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PaymentDetails extends BaseModel {
    private long orderId;
    private String paymentId;
    @Column(name = "payment_link", length = 1024)
    private String PaymentLink;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}