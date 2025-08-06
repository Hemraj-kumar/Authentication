package dev.hemraj.kafka_001.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GeneralUtil {
    private GeneralUtil(){}
    public static String checkIfUserIsAuthenticated(){
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
