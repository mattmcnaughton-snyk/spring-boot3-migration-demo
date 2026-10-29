package com.example.inventory.repository;

import com.example.inventory.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@code @DataJpaTest} moves to {@code org.springframework.boot.data.jpa.test.autoconfigure}
 * in Spring Boot 4.
 */
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void softDeletedProductsAreFilteredOut() {
        Product product = productRepository.saveWithSession(
                new Product("Thingamajig", "Test product", new BigDecimal("1.00"), 1));
        assertThat(productRepository.findByNameContainingIgnoreCase("thingamajig")).hasSize(1);

        productRepository.softDelete(product);
        productRepository.flush();

        assertThat(productRepository.findByNameContainingIgnoreCase("thingamajig")).isEmpty();
    }
}
