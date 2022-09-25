package com.filip.managementapp.controller;

import com.filip.managementapp.model.ShoppingCartItem;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shoppingCarts/{cartId}/{productId}")
public class ShoppingCartItemController {

    @GetMapping
    public ShoppingCartItem getShoppingCartItem(@PathVariable("cartId") Long cartId,
                                                @PathVariable("productId") Long productId) {
        return null;
    }

    @PostMapping
    public ShoppingCartItem saveShoppingCartItem(@PathVariable("cartId") Long cartId,
                                                @PathVariable("productId") Long productId) {
        return null;
    }

    @PutMapping
    public ShoppingCartItem updateShoppingCartItem(@PathVariable("cartId") Long cartId,
                                                 @PathVariable("productId") Long productId) {
        return null;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShoppingCartItem(@PathVariable("cartId") Long cartId,
                                                 @PathVariable("productId") Long productId) {
    }
}
