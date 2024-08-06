package dev.hemraj.jwtauthentication.Service;

import dev.hemraj.jwtauthentication.RequestDto.LoginDto;
import dev.hemraj.jwtauthentication.RequestDto.RegisterDto;
import dev.hemraj.jwtauthentication.Model.User;
import dev.hemraj.jwtauthentication.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    public AuthenticationService(UserRepository userRepository, AuthenticationManager authenticationManager,PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }
    public User register(RegisterDto registerDto){

        try{
            User user = new User();
            user.setEmail(registerDto.getEmail());
            user.setName(registerDto.getFullName());
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
            user.setCreatedAt(new Date());
            return userRepository.save(user);
        }catch (Exception err){
            log.error("Failed to create a user");
            throw new RuntimeException("Error creating a new User!");
        }
    }

    public User authenticate(LoginDto loginDto){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );
        return userRepository.findByEmailIgnoreCase(loginDto.getEmail()).orElseThrow();
    }
    public List<User> allUsers(){
        List<User> all = new ArrayList<>();
        userRepository.findAll().forEach(all::add);
        return all;
    }
}
