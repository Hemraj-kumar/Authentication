package dev.hemraj.kafka_001.service.platform;

import dev.hemraj.kafka_001.model.ApiResponse;
import dev.hemraj.kafka_001.model.CartItem;
import dev.hemraj.kafka_001.model.ErrorBO;
import dev.hemraj.kafka_001.model.Product;
import dev.hemraj.kafka_001.model.dto.platform.AddToCartRequestDto;
import dev.hemraj.kafka_001.repository.CartRepository;
import dev.hemraj.kafka_001.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CartService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public CartService(ProductRepository productRepository, CartRepository cartRepository) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    public ApiResponse addProductsToCart(AddToCartRequestDto request) {
        ApiResponse apiResponse = new ApiResponse();
        List<ErrorBO> errorBOList = new ArrayList<>();
        try {
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                errorBOList.add(new ErrorBO(400, "", "cannot add cart to item as the mandatory field email is missing"));
                apiResponse.setErrorBOList(errorBOList);
                return apiResponse;
            }
            Product productData = productRepository.findById(request.getId());
            if (productData == null) {
                errorBOList.add(new ErrorBO(400, "", "There are no products available for the requested id:" + request.getId()));
                apiResponse.setErrorBOList(errorBOList);
                return apiResponse;
            }
            CartItem cartItem = CartItem.builder()
                    .product(productData)
                    .quantity(request.getQuantity())
                    .email(request.getEmail())
                    .build();
            CartItem cartItem1 = cartRepository.save(cartItem);
            apiResponse.setSuccess(Boolean.TRUE);
            apiResponse.setMessage("Successfully added cart item");
            apiResponse.setData(cartItem1);
            apiResponse.setCode(200);
            return apiResponse;
        } catch (Exception err) {
            log.error("Error in adding product to cart as cartItem : ", err);
        }
        return apiResponse;
    }
    public ApiResponse getCartItems(String userEmail){
        ApiResponse apiResponse = new ApiResponse();
        List<ErrorBO> errorBOList =  new ArrayList<>();
        try{
            List<CartItem> cartItems = cartRepository.findCartItemByEmail(userEmail);
            if(cartItems == null || cartItems.isEmpty()){
                errorBOList.add(new ErrorBO(200,"","No Items to fetch for user email:"+userEmail));
                apiResponse.setErrorBOList(errorBOList);
                apiResponse.setSuccess(Boolean.FALSE);
                apiResponse.setCode(200);
                return apiResponse;
            }
            return ApiResponse.builder()
                    .code(200)
                    .success(Boolean.TRUE)
                    .message("Cart items for user with email"+userEmail+" is fetched successfully!")
                    .data(cartItems).build();

        }catch (Exception err){
            log.error("Error in finding the cart items for user with email:{}",userEmail);
        }
        return apiResponse;
    }

    public void deleteCartItems(String userEmail) {
        try{
           cartRepository.deleteByEmail(userEmail);
        }catch (Exception err){
            log.error("Error in deleting the cart items for user with email:{}",userEmail);
        }

    }


}
