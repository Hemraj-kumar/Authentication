package dev.hemraj.kafka_001.service.platform;

import dev.hemraj.kafka_001.model.*;
import dev.hemraj.kafka_001.model.dto.platform.CreateOrderEvent;
import dev.hemraj.kafka_001.model.dto.platform.OrderItemDto;
import dev.hemraj.kafka_001.repository.CartRepository;
import dev.hemraj.kafka_001.repository.OrderItemRepository;
import dev.hemraj.kafka_001.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, CreateOrderEvent> kafkaTemplate;
    private final JavaMailSenderImpl mailSender;

    public ApiResponse placeOrder(String email) {
        ApiResponse apiResponse = new ApiResponse();
        List<ErrorBO> errorBOList = new ArrayList<>();
        try{
            List<CartItem> cartItems = cartRepository.findCartItemByEmail(email);
            if(cartItems.isEmpty()){
                apiResponse.setCode(200);
                apiResponse.setMessage("Cannot place order since your cart is empty");
                apiResponse.setData(new Object());
                apiResponse.setSuccess(Boolean.TRUE);

                return apiResponse;
            }
            Order order = new Order();
            order.setEmail(email);
            orderRepository.save(order);

            List<OrderItem> orderItems = cartItems.stream()
                    .map(cartItem -> {
                        Product product = cartItem.getProduct();
                        return new OrderItem(
                                product.getId(),
                                product.getProductName(),
                                cartItem.getQuantity(),
                                product.getPrice(),
                                order
                        );
                    })
                    .toList();

            orderItemRepository.saveAll(orderItems);
            order.setOrderItemList(orderItems);
            cartRepository.deleteByEmail(email);
            List<OrderItemDto> orderItemDTOs = orderItems.stream()
                    .map(item -> new OrderItemDto(
                            item.getProductId(),
                            item.getProductName(),
                            item.getQuantity(),
                            item.getPrice()
                    ))
                    .toList();
            produceMessageToKafka(new CreateOrderEvent(email,orderItemDTOs));

            apiResponse.setCode(200);
            apiResponse.setMessage("Order placed successfully!");
            apiResponse.setData(orderItemDTOs);
            apiResponse.setSuccess(Boolean.TRUE);
        }catch (Exception err){
            errorBOList.add(new ErrorBO(500,"","Error in placing order for user with email : "+email));
            log.error("Error in placing order for the user with email: {}",email,err);
        }
        apiResponse.setErrorBOList(errorBOList);
        return apiResponse;
    }
    public void produceMessageToKafka( CreateOrderEvent createOrderEvent) {
        try{
            kafkaTemplate.send("placed-order",createOrderEvent);
        }catch (Exception err){
            log.error("Error in producing message to kafka for useremail: {}",createOrderEvent.getUserEmail(),err);
        }
    }


    @KafkaListener(topics = "placed-order",groupId = "order-group",containerFactory = "kafkaListenerContainerFactory")
    public void consume(CreateOrderEvent createOrderEvent) {
        try {
            log.info("Received Order Event: {}", createOrderEvent);
            try {
                sendEmail(createOrderEvent);
            } catch (Exception e) {
                log.error("Failed to send email to: {}", createOrderEvent.getUserEmail(), e);
            }
        }catch (Exception err){
            log.error("Error in listening to messages : ", err);
        }
    }
    public void sendEmail(CreateOrderEvent createOrderEvent){
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(createOrderEvent.getUserEmail());
            message.setSubject("Your Order is Placed Successfully!");
            message.setText("Thank you for your order. Here are the details:\n" +
                    createOrderEvent.getOrderItems().toString());
            mailSender.send(message);
            log.info("Email sent successfully to user email : {}", createOrderEvent.getUserEmail());
        }catch (Exception err){
            log.error("Error in sending mail to : {} ",createOrderEvent.getUserEmail(),err);
        }
    }
}
