package com.filip.managementapp.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "shopping_cart_items")
@Getter
@Setter
@EqualsAndHashCode
public class ShoppingCartItem {

    @EmbeddedId
    private ShoppingCartItemKey id;

    @ManyToOne
    @MapsId("cartId")
    @JoinColumn(name = "shopping_cart_id")
    private ShoppingCart shoppingCart;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_order_quantity", nullable = false)
    private Long quantity;

    @Column(name = "product_order_price", nullable = false)
    private Double price;
}
