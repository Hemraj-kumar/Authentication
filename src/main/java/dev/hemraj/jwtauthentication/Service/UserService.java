package dev.hemraj.jwtauthentication.Service;

import dev.hemraj.jwtauthentication.Enum.TokenType;
import dev.hemraj.jwtauthentication.Model.ConfirmationToken;
import dev.hemraj.jwtauthentication.Model.ForgotPassword;
import dev.hemraj.jwtauthentication.Model.User;
import dev.hemraj.jwtauthentication.Repository.ConfirmationTokenRepository;
import dev.hemraj.jwtauthentication.Repository.ForgotPasswordRepository;
import dev.hemraj.jwtauthentication.Repository.UserRepository;
import dev.hemraj.jwtauthentication.RequestDto.ProfileDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final ForgotPasswordRepository forgotPasswordRepository;
    public UserService(UserRepository userRepository,
                       EmailService emailService, ConfirmationTokenRepository confirmationTokenRepository, ForgotPasswordRepository forgotPasswordRepository){
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.forgotPasswordRepository = forgotPasswordRepository;
    }
    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
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
    public ResponseEntity<?> changePasswordService(String userEmail){
        try {
            if (!userRepository.existsByEmail(userEmail)) {
                return ResponseEntity.badRequest().body("email does not exists");
            }
            long expiryMillis = 300000;
            Duration expiryDuration = Duration.ofMillis(expiryMillis);
            LocalDateTime createdTime = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
            LocalDateTime expiryTime = LocalDateTime.now().plus(expiryDuration);

            User existingUser = userRepository.findUserByEmailIgnoreCase(userEmail);
            ConfirmationToken confirmationToken = new ConfirmationToken(existingUser, TokenType.FORGOT_PASSWORD);
            String confirmToken = confirmationToken.getConfirmationToken();
            confirmationTokenRepository.save(confirmationToken);

            ForgotPassword forgotPassword = new ForgotPassword();
            forgotPassword.setEmail(userEmail);
            forgotPassword.setToken(confirmToken);
            forgotPassword.setCreated_At(createdTime);
            forgotPassword.setExpires_At(expiryTime);
            forgotPasswordRepository.save(forgotPassword);

            // send mail
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(userEmail);
            mailMessage.setSubject("Change Password for you profile");
            mailMessage.setText("To change your password, click on the following link : " +
                    "http://localhost:4000/confirm-ChangePassword?unique_token=" + confirmToken);
            emailService.sendEmail(mailMessage);

            return ResponseEntity.status(HttpStatus.OK).body("Email sent successfully");
        }catch (Exception err){
            log.error("Exception in generating token/mailSend : ",err);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in sending mail");
    }
    public ResponseEntity<?> confirmChangePassword(String new_password,String token){
        try{
            ForgotPassword userDetails = forgotPasswordRepository.findForgotPasswordByToken(token);
            LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
            if(currentTime.isAfter(userDetails.getExpires_At()) || currentTime.equals(userDetails.getExpires_At())){
                return ResponseEntity.status(HttpStatus.GONE).body("The token/url is expired!");
            }else{
                ConfirmationToken confirmationToken = confirmationTokenRepository.findByConfirmationToken(token);
                User user = confirmationToken.getUserEntity();
                user.setPassword(new_password);
                userRepository.save(user);
            }
        }catch (Exception err){
            log.error("Exception : ",err);
        }
        return ResponseEntity.status(HttpStatus.OK).body("user password updated successfully");
    }
}
