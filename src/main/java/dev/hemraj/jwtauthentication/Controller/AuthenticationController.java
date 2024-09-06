package dev.hemraj.jwtauthentication.Controller;

import dev.hemraj.jwtauthentication.Model.ForgotPassword;
import dev.hemraj.jwtauthentication.RequestDto.LoginDto;
import dev.hemraj.jwtauthentication.RequestDto.PasswordDto;
import dev.hemraj.jwtauthentication.RequestDto.RegisterDto;
import dev.hemraj.jwtauthentication.Model.User;
import dev.hemraj.jwtauthentication.ResponseDto.LoginResponse;
import dev.hemraj.jwtauthentication.Service.AuthenticationService;
import dev.hemraj.jwtauthentication.Service.JwtService;
import dev.hemraj.jwtauthentication.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/public")
@RestController
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserService userService;
    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService,UserService userService){
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.userService = userService;
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody RegisterDto registerDto){
        ResponseEntity<?> registeredUser = authenticationService.register(registerDto);
        return registeredUser;
    }
    @PostMapping("/confirm-account")
    public ResponseEntity<?> confirmUserAccount(@RequestParam("confirmation_token") String token ){
        return authenticationService.confirmEmail(token);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto){
        boolean verified = authenticationService.verification(loginDto.getEmail());
        if(!verified){
            return ResponseEntity.badRequest().body("user email not verified");
        }
        User authenticatedUser = authenticationService.authenticate(loginDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        loginResponse.setUserName(jwtService.extractUsername(jwtToken));
        return ResponseEntity.ok(loginResponse);
    }
    @PatchMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestParam(name = "user_email") String userEmail ){
        try{
            return userService.changePasswordService(userEmail);
        }catch (Exception err){
            log.error("Exception in change password : ", err);
        }
        return ResponseEntity.badRequest().body("Email or Password is not valid!");
    }
    @PostMapping("/confirm-ChangePassword")
    public ResponseEntity<?> confirmChangePassword(@RequestHeader(name = "unique_token") String confirmation_Token, @RequestBody PasswordDto passwordDto) {
        try {
            String new_password = passwordDto.getNew_password();
            String confirmPassword = passwordDto.getConfirm_password();
            if(!confirmPassword.isBlank() && !new_password.isBlank()){
                return userService.confirmNewPassword(confirmPassword,new_password,confirmation_Token);
            }
        } catch (Exception err) {
            log.error("Exception in validating token : ", err);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Token");
    }
}
