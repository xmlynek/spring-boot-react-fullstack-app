package com.filip.managementapp.controller;

import com.filip.managementapp.model.ShoppingCart;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/shoppingCarts")
public class ShoppingCartController {

    @GetMapping
    public List<ShoppingCart> getAllShoppingCarts() {
        return new ArrayList<>();
    }

    @GetMapping("/{cartId}")
    public ShoppingCart getShoppingCartById(@PathVariable("cartId") Long cartId) {
        return null;
    }



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCart createShoppingCart(@RequestBody ShoppingCart shoppingCart) {
        return null;
    }

    @PutMapping("/{cartId}")
    public ShoppingCart updateShoppingCart(@PathVariable("cartId") Long cartId, @RequestBody ShoppingCart shoppingCart) {
        return null;
    }

    @DeleteMapping("/{cartId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShoppingCart(@PathVariable("cartId") Long cartId) {

    }
}
