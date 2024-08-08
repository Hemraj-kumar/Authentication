package dev.hemraj.jwtauthentication.Controller;

import dev.hemraj.jwtauthentication.RequestDto.LoginDto;
import dev.hemraj.jwtauthentication.RequestDto.ProfileDto;
import dev.hemraj.jwtauthentication.RequestDto.RegisterDto;
import dev.hemraj.jwtauthentication.Model.User;
import dev.hemraj.jwtauthentication.ResponseDto.LoginResponse;
import dev.hemraj.jwtauthentication.Service.AuthenticationService;
import dev.hemraj.jwtauthentication.Service.JwtService;
import dev.hemraj.jwtauthentication.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    public AuthenticationController(AuthenticationService authenticationService, JwtService jwtService){
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
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
    //added comments
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
        return ResponseEntity.ok(loginResponse);
    }
}
