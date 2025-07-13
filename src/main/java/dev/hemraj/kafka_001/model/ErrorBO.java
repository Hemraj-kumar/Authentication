package dev.hemraj.kafka_001.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorBO {
    private int code;
    private String field;
    private String desc;
}
