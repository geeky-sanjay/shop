package com.onlineshop.shop.cartAndCheckout.controllers;

import com.onlineshop.shop.cartAndCheckout.models.Cart;
import com.onlineshop.shop.cartAndCheckout.service.ICartItemService;
import com.onlineshop.shop.cartAndCheckout.service.ICartService;
import com.onlineshop.shop.common.dtos.ApiResponse;
import com.onlineshop.shop.common.exceptions.ResourceNotFoundException;
import com.onlineshop.shop.product.exceptions.ProductNotPresentException;
import com.onlineshop.shop.product.services.IProductService;
import com.onlineshop.shop.user.models.User;
import com.onlineshop.shop.user.services.IUserService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api_prefix}/cartItems")
public class CartItemController {
    private final ICartItemService cartItemService;
    private final ICartService cartService;
    private final IProductService productService;
    private final IUserService userService;

    @PostMapping("/item/add")
    public ResponseEntity<ApiResponse> addItemToCart(
                                                     @RequestParam Long productId,
                                                     @RequestParam Integer quantity) {
        try {
            User user = userService.getAuthenticatedUser();
            Cart cart = cartService.initializeNewCart(user);
            cartItemService.addCartItem(cart.getId(), productId, quantity);
            return ResponseEntity.ok().body(new ApiResponse("Item added to cart", null));
        } catch (ProductNotPresentException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch(JwtException e) {
            return ResponseEntity.status(UNAUTHORIZED).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/cart/{cartId}/item/{itemId}/remove")
    public ResponseEntity<ApiResponse> removeItem(@PathVariable Long cartId, @PathVariable Long itemId) {
        try {
            cartItemService.removeCartItem(cartId, itemId);
            return ResponseEntity.ok().body(new ApiResponse("Item removed from cart", null));
        } catch (ResourceNotFoundException | ProductNotPresentException e) {
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/cart/{cartId}/item/{itemId}/update")
    public ResponseEntity<ApiResponse> updateItemQuantity(@PathVariable Long cartId,
                                                  @PathVariable Long itemId,
                                                  @RequestParam Integer quantity) {
        try {
            cartItemService.updateItemQuantity(cartId, itemId, quantity);
            return ResponseEntity.ok().body(new ApiResponse("Item updated", null));
        } catch (ResourceNotFoundException | ProductNotPresentException e) {
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage(), null));
        }
    }
}
