package dev.hemraj.kafka_001.repository;

import dev.hemraj.kafka_001.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product findById(long id);
}
