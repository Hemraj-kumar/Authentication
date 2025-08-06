package dev.hemraj.kafka_001.controller.platform;

import dev.hemraj.kafka_001.model.ApiResponse;
import dev.hemraj.kafka_001.service.platform.OrderService;
import dev.hemraj.kafka_001.utils.GeneralUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Slf4j
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    @PostMapping("/place")
    public ResponseEntity<ApiResponse> createdOrder(){
        ApiResponse response = new ApiResponse();
        String userEmail = GeneralUtil.checkIfUserIsAuthenticated();
        try{
            if(userEmail == null || userEmail.isEmpty()){
                response.setCode(500);
                response.setSuccess(false);
                response.setData(new Object());
                response.setMessage("User with email : {} is not authenticated");
            }
            response = orderService.placeOrder(userEmail);
        }catch (Exception err){
            log.error("Error in placing order for the user with email : {}",userEmail,err);
        }
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
