package dev.hemraj.kafka_001.controller.platform;

import dev.hemraj.kafka_001.model.ApiResponse;
import dev.hemraj.kafka_001.model.dto.platform.AddToCartRequestDto;
import dev.hemraj.kafka_001.service.platform.CartService;
import dev.hemraj.kafka_001.utils.GeneralUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor

public class CartController {
    private final CartService cartService;

    @PostMapping("/addItems")
    public ResponseEntity<ApiResponse> addCart(@RequestBody AddToCartRequestDto cartItem) {
        ApiResponse response = new ApiResponse();
        try{
            String userEmail = GeneralUtil.checkIfUserIsAuthenticated();
            if(userEmail!=null && !userEmail.isEmpty()){
                cartItem.setEmail(userEmail);
                response = cartService.addProductsToCart(cartItem);
                if(response.getErrorBOList()==null || !response.getErrorBOList().isEmpty()){
                    response.setCode(500);
                    response.setSuccess(false);
                    response.setData(new Object());
                }
            }else{
                log.error("User with email : {} is not authenticated", userEmail);
            }
        }catch (Exception err){
            log.error("Error in adding products to cart : ", err);
        }
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/view-items")
    public ResponseEntity<ApiResponse> viewCart() {
        ApiResponse response = new ApiResponse();
        String userEmail = GeneralUtil.checkIfUserIsAuthenticated();
        try{
            if(userEmail == null || userEmail.isEmpty()){
                response.setCode(500);
                response.setSuccess(false);
                response.setData(new Object());
                response.setMessage("User with email : {} is not authenticated");
            }
            response = cartService.getCartItems(userEmail);
        }catch (Exception err){
            log.error("Error in getting cart items for user with email");
        }
        return ResponseEntity.status(response.getCode()).body(response);
    }

}
