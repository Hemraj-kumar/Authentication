package dev.hemraj.jwtauthentication.Controller;

import dev.hemraj.jwtauthentication.RequestDto.LoginDto;
import dev.hemraj.jwtauthentication.RequestDto.RegisterDto;
import dev.hemraj.jwtauthentication.Model.User;
import dev.hemraj.jwtauthentication.ResponseDto.LoginResponse;
import dev.hemraj.jwtauthentication.Service.AuthenticationService;
import dev.hemraj.jwtauthentication.Service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api")
@RestController
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginDto loginDto){


        User authenticatedUser = authenticationService.authenticate(loginDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setExpiresIn(jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }
}
