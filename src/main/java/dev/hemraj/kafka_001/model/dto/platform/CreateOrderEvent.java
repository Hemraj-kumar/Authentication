package dev.hemraj.kafka_001.model.dto.platform;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderEvent implements Serializable {
    private String userEmail;
    private List<OrderItemDto> orderItems;
}
