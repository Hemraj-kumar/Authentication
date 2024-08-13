package dev.hemraj.jwtauthentication.Controller;

import dev.hemraj.jwtauthentication.Model.User;
import dev.hemraj.jwtauthentication.RequestDto.ProfileDto;
import dev.hemraj.jwtauthentication.Service.JwtService;
import dev.hemraj.jwtauthentication.Service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/api/private")
@RestController
@Slf4j
public class UserController {
    private final UserService  userService;
    private final JwtService jwtService;
    public UserController(UserService userService, JwtService jwtService){
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/list-users")
    @PreAuthorize("isAuthenticated()")
    public List<User> getAllUser() {
        try {
            log.info("hitting list-users endpoint");
//            if(jwtService.isTokenValid(token, user)){
                List<User> users = userService.allUsers();
                return users;
//            }else{
//                log.error("token expired!");
//            }
        } catch (Exception  err) {
            log.error("Exception : ", err);
        }
        return new ArrayList<>();
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
