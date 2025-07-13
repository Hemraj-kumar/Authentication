package dev.hemraj.kafka_001.controller;

import dev.hemraj.kafka_001.model.ApiResponse;
import dev.hemraj.kafka_001.model.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    public ResponseEntity<ApiResponse> createUserController(@Valid @RequestBody User user) {

    }
}
