package dev.hemraj.jwtauthentication.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> accessDeniedExceptionHandler(){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid credentials!");
    }
}
