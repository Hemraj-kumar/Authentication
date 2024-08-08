package dev.hemraj.jwtauthentication.Controller;

import dev.hemraj.jwtauthentication.Model.User;
import dev.hemraj.jwtauthentication.RequestDto.ProfileDto;
import dev.hemraj.jwtauthentication.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api")
@RestController
@Slf4j
public class UserController {
    private final UserService  userService;
    public UserController(UserService userService){
        this.userService = userService;
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list-users")
    public List<User> getAllUser(){
        List<User> users = userService.allUsers();
        return users;
    }

    @PutMapping("/profile")
    public ResponseEntity<?> profileDetailsController(@RequestHeader Integer id, @RequestBody ProfileDto profileDto){
        try{
            ResponseEntity<?> updationResponse = userService.updateProfileDetails(id, profileDto);
            return updationResponse;
        }catch (Exception err) {
            log.error("Exception  : ", err);
        }
        return ResponseEntity.badRequest().body("Error in updating profile details");
    }
}
