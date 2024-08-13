package dev.hemraj.jwtauthentication.Service;

import dev.hemraj.jwtauthentication.Model.User;
import dev.hemraj.jwtauthentication.Repository.UserRepository;
import dev.hemraj.jwtauthentication.RequestDto.ProfileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;

    }
    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        log.info("Received request to list users");
        userRepository.findAll().forEach(users::add);
        log.info("returning list of users!");
        return users;
    }

    public ResponseEntity<?> updateProfileDetails(Integer id, ProfileDto profileDto){
        String name = "";
        try{
            //update user_data set about=?, location = ?, designation = ? where id=?;
            User existingUser = userRepository.findUserById(id);
            name = existingUser.getName();
            existingUser.setAbout(profileDto.getAbout());
            existingUser.setLocation(profileDto.getLocation());
            existingUser.setDesgination(profileDto.getDesignation());
            existingUser.setUpdatedAt(new Date());
            userRepository.save(existingUser);
            return ResponseEntity.ok().body(name +" your details are updated successfully!");

        }catch (Exception err){
            log.error("Error in updating profile details!");
        }
        return ResponseEntity.badRequest().body(name +" ,error in updating your details!");

    }
}
