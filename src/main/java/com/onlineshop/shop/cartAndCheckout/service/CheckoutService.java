package com.onlineshop.shop.cartAndCheckout.service;

import com.onlineshop.shop.cartAndCheckout.dtos.CheckoutItemRequestDto;
import com.onlineshop.shop.cartAndCheckout.dtos.StripeResponseDto;
import com.onlineshop.shop.cartAndCheckout.models.PaymentDetails;
import com.onlineshop.shop.cartAndCheckout.repositories.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CheckoutService implements ICheckoutService {

    private final PaymentRepository paymentRepository;

    @Value("${baseURL}")
    private String baseURL;

    @Value("${STRIPE_SECRET_KEY}")
    private String secretKey;

    /**
     * Create price data for a single checkout item.
     */
    private SessionCreateParams.LineItem.PriceData createPriceData(CheckoutItemRequestDto checkoutItemDto) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency(checkoutItemDto.getCurrency() != null ? checkoutItemDto.getCurrency() : "usd")
                .setUnitAmount((long) checkoutItemDto.getPrice() * 100)
                .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(checkoutItemDto.getProductName())
                                .build()
                )
                .build();
    }

    /**
     * Create a line item for the checkout session.
     */
    private SessionCreateParams.LineItem createSessionLineItem(CheckoutItemRequestDto checkoutItemDto) {
        return SessionCreateParams.LineItem.builder()
                .setPriceData(createPriceData(checkoutItemDto))
                .setQuantity((long) checkoutItemDto.getQuantity())
                .build();
    }

    /**
     * Create a Stripe checkout session with the provided items.
     */
    public StripeResponseDto createSession(List<CheckoutItemRequestDto> checkoutItemDtoList) throws StripeException {
        Stripe.apiKey = secretKey;

        String successURL = baseURL + "payment/success";
        String failedURL = baseURL + "payment/failed";

        // Convert checkout items to session line items
        List<SessionCreateParams.LineItem> sessionItemsList = checkoutItemDtoList.stream()
                .map(this::createSessionLineItem)
                .collect(Collectors.toList());

        // Build the session parameters
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.US_BANK_ACCOUNT)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.WECHAT_PAY)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCancelUrl(failedURL)
                .addAllLineItem(sessionItemsList)
                .setSuccessUrl(successURL)
                .build();

        // Create the session
        Session session = Session.create(params);

        // Save payment details in the repository
        PaymentDetails paymentResponse = new PaymentDetails();
        paymentResponse.setPaymentLink(session.getUrl());
        paymentResponse.setOrderId(checkoutItemDtoList.get(0).getOrderId());
        paymentRepository.save(paymentResponse);

        // Return response
        return StripeResponseDto.builder()
                .status("success")
                .message("Checkout session created successfully.")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }
}
