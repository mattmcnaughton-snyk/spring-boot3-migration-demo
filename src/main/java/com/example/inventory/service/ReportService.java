package com.example.inventory.service;

import com.example.inventory.domain.Product;
import com.example.inventory.repository.ProductRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

/**
 * Builds stock reports asynchronously. {@code ListenableFuture} and {@code AsyncResult}
 * were removed in Spring Framework 7; {@link CompletableFuture} replaces them.
 */
@Service
public class ReportService {

    private final ProductRepository productRepository;

    public ReportService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Async
    public CompletableFuture<BigDecimal> totalStockValue() {
        BigDecimal total = productRepository.findAll().stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return CompletableFuture.completedFuture(total);
    }

    @Async
    public CompletableFuture<Long> lowStockCount(int threshold) {
        long count = productRepository.findAll().stream()
                .filter(p -> p.getQuantity() < threshold)
                .map(Product::getId)
                .count();
        return CompletableFuture.completedFuture(count);
    }
}
