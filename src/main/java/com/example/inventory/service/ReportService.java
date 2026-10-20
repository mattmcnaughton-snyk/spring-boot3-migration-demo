package com.example.inventory.service;

import com.example.inventory.domain.Product;
import com.example.inventory.repository.ProductRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.math.BigDecimal;

/**
 * Builds stock reports asynchronously. {@link ListenableFuture} and {@link AsyncResult}
 * are deprecated in Spring Framework 6 and removed in Spring Framework 7 (Spring Boot 4);
 * the replacement is {@code CompletableFuture}.
 */
@Service
public class ReportService {

    private final ProductRepository productRepository;

    public ReportService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Async
    public ListenableFuture<BigDecimal> totalStockValue() {
        BigDecimal total = productRepository.findAll().stream()
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new AsyncResult<>(total);
    }

    @Async
    public ListenableFuture<Long> lowStockCount(int threshold) {
        long count = productRepository.findAll().stream()
                .filter(p -> p.getQuantity() < threshold)
                .map(Product::getId)
                .count();
        return AsyncResult.forValue(count);
    }
}
