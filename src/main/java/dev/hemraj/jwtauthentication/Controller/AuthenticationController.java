package dev.hemraj.jwtauthentication.Controller;

import dev.hemraj.jwtauthentication.RequestDto.LoginDto;
import dev.hemraj.jwtauthentication.RequestDto.PasswordDto;
import dev.hemraj.jwtauthentication.RequestDto.RegisterDto;
import dev.hemraj.jwtauthentication.Model.User;
import dev.hemraj.jwtauthentication.ResponseDto.TokenResponse;
import dev.hemraj.jwtauthentication.Service.AuthenticationService;
import dev.hemraj.jwtauthentication.Service.JwtService;
import dev.hemraj.jwtauthentication.Service.UserService;
import dev.hemraj.jwtauthentication.Service.Utils.Helper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequestMapping("/api/public")
@RestController
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final UserService userService;
    private final Helper helper;
    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService,UserService userService, Helper helper){
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.helper = helper;
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody RegisterDto registerDto){
        return authenticationService.register(registerDto);
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
        int userId  = helper.getUserIdByUser(loginDto.getEmail());

        String jwtToken = jwtService.generateAccessToken(authenticatedUser,String.valueOf(userId));
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser, String.valueOf(userId));

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(jwtToken);
        tokenResponse.setRefreshToken(refreshToken);
        tokenResponse.setExpiresAt(jwtService.getExpirationTime());
        tokenResponse.setStatus(HttpStatus.OK);
        tokenResponse.setSuccess(true);
        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
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

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader(AUTHORIZATION) String refreshToken){
        try {
            if (refreshToken != null && refreshToken.startsWith("Bearer")) {
                String originalRefreshToken = refreshToken.substring(7);

                return jwtService.refreshAccessToken(originalRefreshToken);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Token or Token not found");
            }
        }catch (Exception err){
            log.error("error in refreshing the access token");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("error in refreshing the access token");
    }

}
