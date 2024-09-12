package dev.hemraj.jwtauthentication.Service.Utils;

import dev.hemraj.jwtauthentication.Model.User;
import dev.hemraj.jwtauthentication.Repository.UserRepository;
import dev.hemraj.jwtauthentication.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Slf4j
public class Helper {
    private final UserRepository userRepository;
    public Helper(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public int getUserIdByUser(String email){
        try {
            User user= userRepository.findUserByEmailIgnoreCase(email);
            return user.getId();
        }catch (Exception err){
            log.error("userId not found");
        }
        return 0;
    }

}
