package dev.hemraj.kafka_001.model.dto.platform;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemDto {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
}
