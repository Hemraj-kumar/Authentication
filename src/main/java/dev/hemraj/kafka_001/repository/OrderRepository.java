package dev.hemraj.kafka_001.repository;

import dev.hemraj.kafka_001.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
