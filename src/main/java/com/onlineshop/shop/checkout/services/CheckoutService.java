package com.onlineshop.shop.checkout.services;

import com.onlineshop.shop.checkout.dtos.CheckoutItemDto;
import com.onlineshop.shop.checkout.dtos.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CheckoutService {

    @Value("${baseURL}")
    private String baseURL;

    @Value("${STRIPE_SECRET_KEY}")
    private String secretKey;

    /**
     * Create price data for a single checkout item.
     */
    private SessionCreateParams.LineItem.PriceData createPriceData(CheckoutItemDto checkoutItemDto) {
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
    private SessionCreateParams.LineItem createSessionLineItem(CheckoutItemDto checkoutItemDto) {
        return SessionCreateParams.LineItem.builder()
                .setPriceData(createPriceData(checkoutItemDto))
                .setQuantity((long) checkoutItemDto.getQuantity())
                .build();
    }

    /**
     * Create a Stripe checkout session with the provided items.
     */
    public StripeResponse createSession(List<CheckoutItemDto> checkoutItemDtoList) throws StripeException {
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
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCancelUrl(failedURL)
                .addAllLineItem(sessionItemsList)
                .setSuccessUrl(successURL)
                .build();

        // Create the session
        Session session = Session.create(params);

        // Return response
        return StripeResponse.builder()
                .status("success")
                .message("Checkout session created successfully.")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }
}
