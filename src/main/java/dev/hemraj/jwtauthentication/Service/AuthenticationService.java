package dev.hemraj.jwtauthentication.Service;

import dev.hemraj.jwtauthentication.Enum.TokenType;
import dev.hemraj.jwtauthentication.Model.ConfirmationToken;
import dev.hemraj.jwtauthentication.Repository.ConfirmationTokenRepository;
import dev.hemraj.jwtauthentication.RequestDto.LoginDto;
import dev.hemraj.jwtauthentication.RequestDto.RegisterDto;
import dev.hemraj.jwtauthentication.Model.User;
import dev.hemraj.jwtauthentication.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailService emailService;
    public AuthenticationService(UserRepository userRepository, AuthenticationManager authenticationManager,PasswordEncoder passwordEncoder,
                                 ConfirmationTokenRepository confirmationTokenRepository,EmailService emailService){
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailService = emailService;
    }
    public ResponseEntity<?> register(RegisterDto registerDto){
        try {
            if (userRepository.existsByEmail(registerDto.getEmail())) {
                return ResponseEntity.badRequest().body("user email already in use!");
            }
            User user = new User();
            user.setEmail(registerDto.getEmail());
            user.setName(registerDto.getFullName());
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
            user.setCreatedAt(new Date());
            user.setVerified(false);
            userRepository.save(user);

            ConfirmationToken confirmationToken = new ConfirmationToken(user, TokenType.SIGNUP_CONFIRMATION);
            confirmationTokenRepository.save(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("user confirmation mail ");
            mailMessage.setText("To confirm your account, please click here : "
                    + "http://localhost:4000/confirm-account?confirmation_token=" + confirmationToken.getConfirmationToken());
            emailService.sendEmail(mailMessage);

            log.info("confirmation mail successfully sent!");

            return ResponseEntity.ok("Verify email by the link sent on your email address!");

        } catch (Exception err) {
            log.error("Failed to create a user");
            throw new RuntimeException("Error creating a new User!");
        }
    }

    public ResponseEntity<?> confirmEmail(String confirmationToken ){
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
        if(token!=null){
            User user = userRepository.findUserByEmailIgnoreCase(token.getUserEntity().getEmail());
            log.info("users email : {}",user.getEmail());
            user.setVerified(true);
            userRepository.save(user);
            log.info(user.toString());
            return ResponseEntity.ok("user email verified successfully!");
        }
        return ResponseEntity.badRequest().body("error : couldn't verify email!");
    }

    public User authenticate(LoginDto loginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );
        return userRepository.findByEmailIgnoreCase(loginDto.getEmail()).orElseThrow();

    }
    public boolean verification(String email ){
        boolean verified = userRepository.findUserByEmailIgnoreCase(email).isVerified();
        return verified;
    }

    public List<User> allUsers(){
        List<User> all = new ArrayList<>();
        userRepository.findAll().forEach(all::add);
        return all;
    }
}
