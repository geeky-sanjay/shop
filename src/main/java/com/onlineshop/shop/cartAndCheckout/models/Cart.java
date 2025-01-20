package com.onlineshop.shop.cartAndCheckout.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.onlineshop.shop.common.models.BaseModel;
import com.onlineshop.shop.user.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Cart extends BaseModel {

    private BigDecimal totalAmount = BigDecimal.ZERO;

    // - `mappedBy = "cart"`: The "cart" field in the associated entity owns the relationship.
    // - `cascade = CascadeType.ALL`: Cascades all operations to associated entities.
    // - `orphanRemoval = true`: Removes associated entities if no longer referenced.
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    // - `mappedBy = "cart"`: The "cart" field in the associated entity owns the relationship.
    @JsonBackReference // Used to break the infinite recursion problem
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void addItem(CartItem cartItem) {
        cartItems.add(cartItem);
        cartItem.setCart(this);
        updateTotalAmount();
    }

    public void removeItem(CartItem cartItem) {
        cartItems.remove(cartItem);
        cartItem.setCart(null);
        updateTotalAmount();
    }

    private void updateTotalAmount() {
        this.totalAmount = cartItems.stream()
                .map(item -> {
                    BigDecimal unitPrice = item.getUnitPrice();
                    if(unitPrice == null) {
                        return BigDecimal.ZERO;
                    }
                    return unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
}
