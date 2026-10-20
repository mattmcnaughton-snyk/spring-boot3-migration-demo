package com.example.inventory.repository;

import com.example.inventory.domain.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

/**
 * Uses the native Hibernate {@link Session} API directly. {@code Session.save()} and
 * {@code Session.update()} are deprecated in Hibernate 6 and removed in Hibernate 7
 * (the version Spring Boot 4 ships with).
 */
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Product saveWithSession(Product product) {
        Session session = entityManager.unwrap(Session.class);
        session.save(product);
        return product;
    }

    @Override
    @Transactional
    public void softDelete(Product product) {
        product.setDeleted(true);
        entityManager.unwrap(Session.class).update(product);
    }
}
