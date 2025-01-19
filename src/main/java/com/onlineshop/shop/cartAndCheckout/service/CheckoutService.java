package com.onlineshop.shop.cartAndCheckout.service;

import com.onlineshop.shop.cartAndCheckout.dtos.CheckoutItemRequestDto;
import com.onlineshop.shop.cartAndCheckout.dtos.StripeResponseDto;
import com.onlineshop.shop.cartAndCheckout.models.PaymentDetails;
import com.onlineshop.shop.cartAndCheckout.repositories.PaymentRepository;
import com.onlineshop.shop.order.models.Order;
import com.onlineshop.shop.order.models.OrderStatus;
import com.onlineshop.shop.order.repositories.OrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.onlineshop.shop.cartAndCheckout.models.PaymentStatus.SUCCESS;

@RequiredArgsConstructor
@Service
public class CheckoutService implements ICheckoutService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository; // Inject OrderRepository

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

        // Update the order status if payment is successful
        updateOrderStatus(checkoutItemDtoList.get(0).getOrderId());

        // Return response
        return StripeResponseDto.builder()
                .status(SUCCESS)
                .message("Checkout session created successfully.")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }

    /**
     * Update the status of an order if payment is successful.
     */
    private void updateOrderStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        order.setOrderStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);
    }
}
