package dev.hemraj.kafka_001.model.dto.platform;

import lombok.Data;

@Data
public class AddToCartRequestDto {
    private long id;
    private int quantity;
}
