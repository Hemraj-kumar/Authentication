package dev.hemraj.kafka_001.controller.platform;

import dev.hemraj.kafka_001.model.ApiResponse;
import dev.hemraj.kafka_001.model.dto.platform.AddToCartRequestDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/cart")
public class CartController {
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addCart(@Valid @RequestBody AddToCartRequestDto cartItem) {
        ApiResponse response = new ApiResponse();
        try{
            String userEmail = checkIfUserIsAuthenticated();
            if(userEmail!=null && !userEmail.isEmpty()){
                response =
            }else{
                log.error("User with email : {} is not authenticated", userEmail);
            }
        }catch (Exception err){
            log.error("Error in adding products to cart : ", err);
        }
        return ResponseEntity.status(response.getCode()).body(response);
    }

    private String checkIfUserIsAuthenticated(){
        String userEmail="";
        try{
            userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            if(userEmail == null || userEmail.isEmpty()){
                return userEmail;
            }
        }catch (Exception err){
            log.error("Error in checking if user is authenticated : ", err);
        }
        return userEmail;
    }
}
