package dev.hemraj.kafka_001.repository;

import dev.hemraj.kafka_001.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findCartItemByEmail(String email);
    void deleteByEmail(String email);
}
