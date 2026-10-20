package com.example.inventory.repository;

import com.example.inventory.domain.Product;

public interface ProductRepositoryCustom {

    Product saveWithSession(Product product);

    void softDelete(Product product);
}
