package dev.hemraj.kafka_001.model.dto;

import lombok.Data;

@Data
public class LoginReponseDto {
    private int id;
    private String token;
    private String expiresIn;
}
