package dev.hemraj.jwtauthentication.Service;

import dev.hemraj.jwtauthentication.Model.ForgotPassword;
import dev.hemraj.jwtauthentication.Model.User;

import dev.hemraj.jwtauthentication.Repository.ForgotPasswordRepository;
import dev.hemraj.jwtauthentication.Repository.UserRepository;
import dev.hemraj.jwtauthentication.RequestDto.ProfileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.*;
import java.util.*;


@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository,
                       EmailService emailService,
                       ForgotPasswordRepository forgotPasswordRepository,PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public ResponseEntity<?> updateProfileDetails(Integer id, ProfileDto profileDto){
        String name = "";
        try{
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
    public ResponseEntity<?> changePasswordService(String userEmail){
        try {
            if (!userRepository.existsByEmail(userEmail)) {
                return ResponseEntity.badRequest().body("email does not exists");
            }
            ForgotPassword user = new ForgotPassword();
            long expiryMillis = 300000;
            Duration expiryDuration = Duration.ofMillis(expiryMillis);
            LocalDateTime createdTime = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
            LocalDateTime expiryTime = LocalDateTime.now().plus(expiryDuration);

            String token = UUID.randomUUID().toString();
            user.setEmail(userEmail);
            user.setToken(token);
            user.setCreatedAt(createdTime);
            user.setExpiresAt(expiryTime);
            forgotPasswordRepository.save(user);

            // send mail
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(userEmail);
            mailMessage.setSubject("Change Password for you profile");
            mailMessage.setText("To change your password, click on the following link : " +
                    "http://localhost:4000/confirm-ChangePassword?unique_token=" + token);
            emailService.sendEmail(mailMessage);

            return ResponseEntity.status(HttpStatus.OK).body("Email sent successfully");
        }catch (Exception err){
            log.error("Exception in generating token/mailSend : ",err);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in sending mail");
    }
    public ResponseEntity<?> confirmNewPassword(String confirmPassword,String newPassword,String token){
        try {

            ForgotPassword person = forgotPasswordRepository.findForgotPasswordByToken(token);
            if (person == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User does not exist");
            }
            if(LocalDateTime.now().isAfter(person.getExpiresAt())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token/Link is Expired!");
            }
            String email = person.getEmail();
            User existingUser = userRepository.findUserByEmailIgnoreCase(email);
            String oldPassword = existingUser.getPassword();
            log.info("old password : {}",oldPassword);

            if (newPassword.equals(confirmPassword)) {
                if (!passwordEncoder.matches(newPassword, oldPassword)) {
                    person.setNewPassword(passwordEncoder.encode(newPassword));
                    person.setConfirm_newPassword(passwordEncoder.encode(confirmPassword));
                    forgotPasswordRepository.save(person);

                    existingUser.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(existingUser);

                    return ResponseEntity.status(HttpStatus.OK).body("User password updated successfully");
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password already exist");
                }
            } else {
                return ResponseEntity.status(HttpStatus.IM_USED).body("New password and confirm password do not match");
            }
        } catch (Exception err) {
            log.error("Exception: ", err);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the password");
        }
    }


    public ResponseEntity<?> uploadImageService(String uploadDirectory , MultipartFile userImage) throws IOException {
        try {
            String uniqueFileName = UUID.randomUUID()+"_"+userImage.getOriginalFilename();
            Path uploadPath = Path.of(uploadDirectory);
            Path filePath = uploadPath.resolve(uniqueFileName);

            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
            Files.copy(userImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return ResponseEntity.status(HttpStatus.OK).body("image uploaded successfully : "+uniqueFileName);
        } catch (Exception err) {
            log.error("Error in uploading image : ", err);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image uploaded successfully");
    }
    public ResponseEntity<?> getImageByName(String imageId,String fileDirectory){
        Path imagePath = Path.of(fileDirectory, imageId);
        try{
            if(Files.exists(imagePath)){
                byte[] imageBytes = Files.readAllBytes(imagePath);
                String contentType = Files.probeContentType(imagePath);
                if(contentType == null){
                    contentType = "application/octet-stream";
                }
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(contentType));
                return ResponseEntity.status(HttpStatus.OK)
                        .headers(headers)
                        .body(imageBytes);
            }
        }catch (Exception err){
            log.error("Exception in fetching image : ",err);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in fetching image");
    }
    public ResponseEntity<?> deleteImage(String imageId, String directory){
        try{
            Path deletePath = Path.of(directory, imageId);
            if(Files.exists(deletePath)){
                Files.deleteIfExists(deletePath);
                return ResponseEntity.status(HttpStatus.OK).body("image deleted successfully");
            }
        }catch (Exception err){
            log.error("Error in deleting a image : ",err);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in deleting the image");
    }

}
