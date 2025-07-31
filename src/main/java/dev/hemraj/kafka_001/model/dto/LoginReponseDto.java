package dev.hemraj.kafka_001.model.dto;

import lombok.Data;

@Data
public class LoginReponseDto {
    private long id;
    private String token="";
}
