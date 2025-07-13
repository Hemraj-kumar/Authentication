package dev.hemraj.kafka_001.service.authentication;

import dev.hemraj.kafka_001.exception.InvalidCredentialsException;
import dev.hemraj.kafka_001.exception.UserAlreadyExistsException;
import dev.hemraj.kafka_001.model.ApiResponse;
import dev.hemraj.kafka_001.model.User;
import dev.hemraj.kafka_001.model.dto.LoginDto;
import dev.hemraj.kafka_001.model.dto.RegisterUserDto;
import dev.hemraj.kafka_001.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }
    ApiResponse apireponse = new ApiResponse();

    public User signup(RegisterUserDto data){
        try {
            if (userRepository.findByEmail(data.getEmail()).isPresent()) {
                throw new UserAlreadyExistsException("User with email " + data.getEmail() + " already exists");
            }
            User user = new User();
            user.setName(data.getName());
            user.setEmail(data.getEmail());
            user.setPassword(passwordEncoder.encode(data.getPassword()));

            return userRepository.save(user);
        } catch (Exception err) {
            log.error("Error in creating user : ",err);
        }
    }
    public User authenticate(LoginDto data){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword())
            );
            return userRepository.findByEmail(data.getEmail()).orElseThrow();
        }catch (AuthenticationException err){
            throw new InvalidCredentialsException("Invalid Email/password");
        }
        return userRepository.findByEmail(data.getEmail()).orElseThrow(()->new UsernameNotFoundException("User not found with email: " + data.getEmail())));
    }
}
