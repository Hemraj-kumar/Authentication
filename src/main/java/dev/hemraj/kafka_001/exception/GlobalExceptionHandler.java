package dev.hemraj.kafka_001.exception;

import dev.hemraj.kafka_001.model.ApiResponse;
import dev.hemraj.kafka_001.model.ErrorBO;
import dev.hemraj.kafka_001.utils.ApiConstants;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException exception) {
        List<ErrorBO> errors = exception.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> new ErrorBO(ApiConstants.GENERIC_VALIDATION_ERROR, e.getField(),e.getDefaultMessage()))
                .collect(Collectors.toList());
        ApiResponse response = new ApiResponse();
        response.setCode(400);
        response.setErrorBOList(errors);
        response.setSuccess(Boolean.FALSE);
        response.setData(new JSONObject());

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException exception){
        ErrorBO errors = new ErrorBO();
        errors.setCode(400);
        errors.setDesc(exception.getMessage());
        errors.setField("email");
        List<ErrorBO> errorBOList = List.of(errors);

        ApiResponse response = new ApiResponse();
        response.setCode(409);
        response.setErrorBOList(errorBOList);
        response.setSuccess(Boolean.FALSE);
        response.setData(new JSONObject());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);

    }
}
