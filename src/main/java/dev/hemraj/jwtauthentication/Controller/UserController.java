package dev.hemraj.jwtauthentication.Controller;

import dev.hemraj.jwtauthentication.Model.ImageData;
import dev.hemraj.jwtauthentication.Model.User;
import dev.hemraj.jwtauthentication.RequestDto.ProfileDto;
import dev.hemraj.jwtauthentication.Service.JwtService;
import dev.hemraj.jwtauthentication.Service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOError;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequestMapping("/api/private")
@RestController
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
    }

    @GetMapping("/list-users")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<User>> getAllUser() {
        try {
            List<User> users = userService.allUsers();
            return ResponseEntity.ok(users);
        } catch (ExpiredJwtException err) {
            log.error("Token Expiration error : ", err);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        } catch (Exception err) {
            log.error("Exception : ", err);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> profileDetailsController(@RequestHeader Integer id, @RequestBody ProfileDto profileDto) {
        try {
            ResponseEntity<?> updationResponse = userService.updateProfileDetails(id, profileDto);
            return updationResponse;
        } catch (Exception err) {
            log.error("Exception  : ", err);
        }
        return ResponseEntity.badRequest().body("Error in updating profile details");
    }

    @PostMapping("/image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadImagesForProfile(@RequestParam("image")MultipartFile imageFile) throws IOException {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userService.uploadImageService(imageFile));
        }catch (Exception err){
            log.error("Exception  : ",err);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in uploading image!");
    }

    @GetMapping("/info/{name}")
    public ResponseEntity<?>  getImageInfoByName(@RequestParam("name") String name){
        ResponseEntity<ImageData> image = userService.getImageByName(name);

        return ResponseEntity.status(HttpStatus.OK)
                .body(image);
    }

    @GetMapping("/image/{name}")
    public ResponseEntity<?>  getImageByName(@RequestParam("name") String name){
        try{
            return userService.getImage(name);
        }catch (Exception err){
            log.error("Exception  : ",err);
        }
        return null;
    }
}
