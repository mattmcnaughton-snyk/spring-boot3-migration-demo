package com.example.inventory.repository;

import com.example.inventory.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    List<Product> findByNameContainingIgnoreCase(String name);
}
