package com.filip.managementapp.service;

import com.filip.managementapp.exception.ResourceNotFoundException;
import com.filip.managementapp.model.ShoppingCartItem;
import com.filip.managementapp.model.ShoppingCartItemKey;
import com.filip.managementapp.repository.ShoppingCartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartItemService {

    public static final String SHOPPING_CART_ITEM_BY_ID_NOT_FOUND_STRING =
            "Shopping cart item with cart id '%d' and product id '%d' not found";

    private final ShoppingCartItemRepository shoppingCartItemRepository;

    private ShoppingCartItem findShoppingCartItemById(Long cartId, Long productId) {
        return shoppingCartItemRepository.findById(new ShoppingCartItemKey(cartId, productId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(SHOPPING_CART_ITEM_BY_ID_NOT_FOUND_STRING, cartId, productId))
                );
    }

    private ShoppingCartItem updateShoppingCartItem(Long cartId, Long productId, ShoppingCartItem updateRequestBody) {
        return null;
    }
}
