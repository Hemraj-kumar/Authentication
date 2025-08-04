package dev.hemraj.kafka_001.controller.platform;

import dev.hemraj.kafka_001.model.ApiResponse;
import dev.hemraj.kafka_001.model.Product;
import dev.hemraj.kafka_001.service.platform.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllProducts() {
        ApiResponse response = new ApiResponse();
        try {
            response = productService.fetchAllProducts();
            if(!response.getErrorBOList().isEmpty()) {
                response.setSuccess(Boolean.FALSE);
                response.setCode(HttpStatus.BAD_REQUEST.value());
                response.setData(new Object());
            }
        } catch (Exception err) {
            response.setCode(500);
            response.setSuccess(Boolean.FALSE);
            response.setData("Internal Server error!");
            log.error("Error in fetching product details from database : ", err);
        }
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @PostMapping("/seeder")
    public ResponseEntity<ApiResponse> addProduct(@Valid @RequestBody Product product){
        ApiResponse response = new ApiResponse();
        try {
            response = productService.addProductsToDatabase(product);
            if(!response.getErrorBOList().isEmpty()) {
                response.setSuccess(Boolean.FALSE);
                response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.setData(new Object());
            }
        }catch (Exception err){
            response.setCode(500);
            response.setSuccess(Boolean.FALSE);
            response.setData("Internal Server error!");
            log.error("Error in adding product to the database : ", err);
        }
        return ResponseEntity.status(response.getCode()).body(response);
    }


}
