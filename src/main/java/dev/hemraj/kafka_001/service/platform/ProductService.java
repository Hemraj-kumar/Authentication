package dev.hemraj.kafka_001.service.platform;

import dev.hemraj.kafka_001.model.ApiResponse;
import dev.hemraj.kafka_001.model.ErrorBO;
import dev.hemraj.kafka_001.model.Product;
import dev.hemraj.kafka_001.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final View error;

    public ApiResponse fetchAllProducts(){
        ApiResponse apiResponse =new ApiResponse();
        List<ErrorBO> errorBOList = new ArrayList<>();
        try{
            List<Product> productsList = productRepository.findAll();
            if(productsList.isEmpty()){
                apiResponse.setMessage("There are no products to list");
            }
            apiResponse.setData(productsList);
            apiResponse.setSuccess(Boolean.TRUE);
            apiResponse.setCode(200);
            apiResponse.setMessage("fetched all products successfully");
        }catch (Exception err){
            log.error("Error in fetching products list : ", err);
            errorBOList.add(new ErrorBO(500,"","Error in fetching the products list"));
        }
        apiResponse.setErrorBOList(errorBOList);
        return apiResponse;
    }

    public ApiResponse addProductsToDatabase(Product product){
        ApiResponse apiResponse = new ApiResponse();
        List<ErrorBO> errorBOList = new ArrayList<>();
        try{
            if(product!=null){
                Product savedProduct = productRepository.save(product);
                apiResponse.setData(savedProduct);
                apiResponse.setSuccess(Boolean.TRUE);
                apiResponse.setMessage("Data saved successfully");
                apiResponse.setCode(200);
            }else{
                errorBOList.add(new ErrorBO(500,"","Error in saving the product"));
            }
        }catch (Exception err){
            log.error("Error in adding product to the database : ", err);
            errorBOList.add(new ErrorBO(500,"","Error in adding product to the database "));
        }
        apiResponse.setErrorBOList(errorBOList);
        return apiResponse;
    }
}
