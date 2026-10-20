package com.example.inventory.web;

import com.example.inventory.domain.Product;
import com.example.inventory.service.ProductService;
import com.example.inventory.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ReportService reportService;

    public ProductController(ProductService productService, ReportService reportService) {
        this.productService = productService;
        this.reportService = reportService;
    }

    @GetMapping
    public List<Product> list() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Product create(@Valid @RequestBody Product product) {
        return productService.create(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return productService.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public List<Map<String, Object>> search(@RequestParam String q,
                                            @RequestParam(required = false) String sort) {
        return productService.search(q, sort);
    }

    @GetMapping("/by-name")
    public List<Product> byName(@RequestParam String name) {
        return productService.findByExactName(name);
    }

    @GetMapping("/report")
    public Map<String, Object> report(@RequestParam(defaultValue = "5") int threshold)
            throws ExecutionException, InterruptedException {
        BigDecimal total = reportService.totalStockValue().get();
        long lowStock = reportService.lowStockCount(threshold).get();
        return Map.of("totalStockValue", total, "lowStockCount", lowStock);
    }
}
