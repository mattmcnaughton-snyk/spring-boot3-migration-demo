package com.example.inventory.service;

import com.example.inventory.domain.Product;
import com.example.inventory.repository.ProductRepository;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final JdbcTemplate jdbcTemplate;

    public ProductService(ProductRepository productRepository, JdbcTemplate jdbcTemplate) {
        this.productRepository = productRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product create(Product product) {
        return productRepository.saveWithSession(product);
    }

    public boolean delete(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.softDelete(product);
                    return true;
                })
                .orElse(false);
    }

    /**
     * VULNERABLE: SQL injection. The search term and sort column are concatenated
     * straight into the query string.
     */
    public List<Map<String, Object>> search(String term, @Nullable String sort) {
        String orderBy = sort == null ? "name" : sort;
        String sql = "SELECT id, name, description, price, quantity FROM products "
                + "WHERE deleted = false AND name LIKE '%" + term + "%' ORDER BY " + orderBy;
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * VULNERABLE: SQL injection via string concatenation into a row-mapped query.
     */
    public List<Product> findByExactName(String name) {
        return jdbcTemplate.query(
                "SELECT * FROM products WHERE name = '" + name + "'",
                new BeanPropertyRowMapper<>(Product.class));
    }
}
