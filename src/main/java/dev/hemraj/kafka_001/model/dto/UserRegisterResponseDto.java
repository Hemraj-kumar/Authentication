package dev.hemraj.kafka_001.model.dto;

import lombok.Data;

@Data
public class UserRegisterResponseDto {
    private String id;
    private String email;
    private String name;
}
