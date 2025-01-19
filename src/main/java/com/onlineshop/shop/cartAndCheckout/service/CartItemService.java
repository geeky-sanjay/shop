package com.onlineshop.shop.cartAndCheckout.service;

import com.onlineshop.shop.cartAndCheckout.models.Cart;
import com.onlineshop.shop.cartAndCheckout.models.CartItem;
import com.onlineshop.shop.cartAndCheckout.repositories.CartItemRepository;
import com.onlineshop.shop.cartAndCheckout.repositories.CartRepository;
import com.onlineshop.shop.common.exceptions.ResourceNotFoundException;
import com.onlineshop.shop.product.exceptions.ProductNotPresentException;
import com.onlineshop.shop.product.models.Product;
import com.onlineshop.shop.product.services.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final IProductService productService;
    private final ICartService cartService;

    @Override
    public void addCartItem(Long cartId, Long productId, int quantity) throws ProductNotPresentException {
        // 1. Get the cart
        // 2. Get the product
        // 3. Check if the product already exists in the cart
        // 4. If it exists, update the quantity
        // 5. If it doesn't exist, add the product to the cart
        System.out.println("Adding item to cart " + productId + quantity);
        Cart cart = cartService.getCart(cartId);
        Product product = productService.getProductById(productId);
        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElse(new CartItem());
        if (cartItem.getId() == null) {
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());
        }
        else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cartItem.setTotalPrice();
        cart.addItem(cartItem);

        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void removeCartItem(Long cartId, Long productId) throws ProductNotPresentException {
        // 1. Get the cart
        // 2. Get the product
        // 3. Check if the product exists in the cart
        // 4. If it exists, remove the product from the cart
        // 5. If it doesn't exist, throw an exception

        Cart cart = cartService.getCart(cartId);
        Product product = productService.getProductById(productId);
        CartItem cartItem = getCartItem(cartId, productId);
        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void updateItemQuantity(Long cartId, Long productId, int quantity) throws ProductNotPresentException {
        // 1. Get the cart
        // 2. Get the product
        // 3. Check if the product exists in the cart
        // 4. If it exists, update the quantity
        // 5. If it doesn't exist, throw an exception

        Cart cart = cartService.getCart(cartId);
        Product product = productService.getProductById(productId);
        cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    item.setUnitPrice(item.getProduct().getPrice());
                    item.setTotalPrice();
                });
        BigDecimal totalAmount = cart.getTotalAmount();
        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        return cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));
    }
}
