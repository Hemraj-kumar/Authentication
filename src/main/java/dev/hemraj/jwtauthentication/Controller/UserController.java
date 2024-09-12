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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequestMapping("/api/private")
@RestController
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    private static final String DIRECTORY_URI = "/Users/fi-user/Documents/PC/images";
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
            return ResponseEntity.status(HttpStatus.OK).body(userService.uploadImageService(DIRECTORY_URI,imageFile));
        }catch (Exception err){
            log.error("Exception  : ",err);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in uploading image!");
    }

    @GetMapping("/getImages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getImageService(@RequestParam("imageId") String imageId){
        try {
            return userService.getImageByName(imageId, DIRECTORY_URI);
        }catch (Exception err){
            log.error("Exception in retrieving image :",err);
        }
      return null;
    }
    @DeleteMapping("/deleteImage")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteImageById(@RequestParam("imageId") String imageId){
        try{
            return userService.deleteImage(imageId, DIRECTORY_URI);
        }catch (Exception err){
            log.error("Error in deleting the image");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in deleting the image");
    }
}
