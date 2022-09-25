package com.filip.managementapp.repository;

import com.filip.managementapp.model.ShoppingCartItem;
import com.filip.managementapp.model.ShoppingCartItemKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartItemRepository extends JpaRepository<ShoppingCartItem, ShoppingCartItemKey> {
}
