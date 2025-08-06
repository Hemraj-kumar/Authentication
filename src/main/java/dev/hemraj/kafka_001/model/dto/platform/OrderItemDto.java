package dev.hemraj.kafka_001.model.dto.platform;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto implements Serializable {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
}
