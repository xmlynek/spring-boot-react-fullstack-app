package com.filip.managementapp.repository;

import com.filip.managementapp.AbstractRepositoryTest;
import com.filip.managementapp.model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ProductRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private final Product product;

    public ProductRepositoryTest() {
        this.product = new Product(
                null,
                "Product 123",
                "Great product",
                "Great product made in unknown country",
                123L,
                55.43,
                true
        );
    }

    @AfterEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void existsByNameShouldReturnTrue() {
        productRepository.save(product);

        boolean result = productRepository.existsByName(product.getName());

        assertTrue(result);
    }

    @Test
    void existsByNameShouldReturnFalse() {
        productRepository.save(product);

        boolean result = productRepository.existsByName("Random name");

        assertFalse(result);
    }
}