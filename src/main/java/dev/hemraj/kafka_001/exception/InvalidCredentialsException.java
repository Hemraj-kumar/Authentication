package dev.hemraj.kafka_001.exception;

public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(String message){
        super(message);
    }
    public InvalidCredentialsException(String message, Throwable cause){
        super(message, cause);
    }
}
