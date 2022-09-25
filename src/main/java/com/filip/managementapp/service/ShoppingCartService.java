package com.filip.managementapp.service;

import com.filip.managementapp.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;

}
