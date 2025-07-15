package dev.hemraj.kafka_001.controller;

import dev.hemraj.kafka_001.model.ApiResponse;
import dev.hemraj.kafka_001.model.User;
import dev.hemraj.kafka_001.model.dto.RegisterUserDto;
import dev.hemraj.kafka_001.service.authentication.AuthService;
import dev.hemraj.kafka_001.service.authentication.JwtService;
import jakarta.servlet.ServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> createUserController(@Valid @RequestBody RegisterUserDto user) {
        ApiResponse response = new ApiResponse();
        try{
            response = authService.signup(user);
        }catch (Exception err){
            response.setCode(500);
            response.setSuccess(Boolean.FALSE);
            response.setData("Internal Server error!");
            log.error("Error in creating user with user request body : {}",user,err);
        }
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
