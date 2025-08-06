package dev.hemraj.kafka_001.model.dto.platform;

import dev.hemraj.kafka_001.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateOrderEvent {
    private String userEmail;
    private List<OrderItemDto> orderItems;
}
