package dev.hemraj.kafka_001.repository;


import dev.hemraj.kafka_001.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

}
