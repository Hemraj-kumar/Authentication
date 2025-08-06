package dev.hemraj.kafka_001.model.dto.platform;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartRequestDto {
    private long id;
    private int quantity;
    private String email;
}
