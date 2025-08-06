package dev.hemraj.kafka_001.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "orderItems")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Long productId;
    private String productName;
    private int quantity;
    private double price;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderItem(Long productId, String productName, double price, int quantity, Order order) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.order = order;
    }
}
