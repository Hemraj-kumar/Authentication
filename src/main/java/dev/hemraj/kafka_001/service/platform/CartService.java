package dev.hemraj.kafka_001.service.platform;

import dev.hemraj.kafka_001.model.ApiResponse;
import dev.hemraj.kafka_001.model.dto.platform.AddToCartRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CartService {
    public ApiResponse addProductsToCart(AddToCartRequestDto addToCartRequestDto) {
        ApiResponse apiResponse = new ApiResponse();
        try{

        }catch (Exception err){
            log.error("Error in adding product to cart as cartItem : ", err);
        }
        return apiResponse;
    }

}
