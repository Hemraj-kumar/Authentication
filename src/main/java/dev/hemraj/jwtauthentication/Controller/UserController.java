package dev.hemraj.jwtauthentication.Controller;

import dev.hemraj.jwtauthentication.Model.User;
import dev.hemraj.jwtauthentication.RequestDto.ProfileDto;
import dev.hemraj.jwtauthentication.Service.JwtService;
import dev.hemraj.jwtauthentication.Service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
    public ResponseEntity<List<User>> getAllUser() {
        try {
            List<User> users = userService.allUsers();
            return ResponseEntity.ok(users);
        }catch (ExpiredJwtException err){
            log.error("Token Expiration error : ",err);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }
        catch (Exception  err) {
            log.error("Exception : ", err);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> profileDetailsController(@RequestHeader Integer id, @RequestBody ProfileDto profileDto){
        try{
            ResponseEntity<?> updationResponse = userService.updateProfileDetails(id, profileDto);
            return updationResponse;
        }catch (Exception err) {
            log.error("Exception  : ", err);
        }
        return ResponseEntity.badRequest().body("Error in updating profile details");
    }

//    @PostMapping("confirm-ChangePassword")
//    public ResponseEntity<?> confirmChangePassword(@RequestParam(name = "unique_token") String confirmation_Token){
//        try{
//            ResponseEntity<?> validationResponse = userService.validateTokenForPassword(confirmation_Token);
//            return ResponseEntity.status(HttpStatus.OK).body("password changed successfully");
//        }catch (Exception err){
//            log.error("Exception in validating token : ",err);
//        }
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Token");
//    }
}
