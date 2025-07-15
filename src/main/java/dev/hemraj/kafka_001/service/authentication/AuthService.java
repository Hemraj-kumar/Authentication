package dev.hemraj.kafka_001.service.authentication;

import dev.hemraj.kafka_001.exception.InvalidCredentialsException;
import dev.hemraj.kafka_001.exception.UserAlreadyExistsException;
import dev.hemraj.kafka_001.model.ApiResponse;
import dev.hemraj.kafka_001.model.User;
import dev.hemraj.kafka_001.model.dto.LoginDto;
import dev.hemraj.kafka_001.model.dto.RegisterUserDto;
import dev.hemraj.kafka_001.model.dto.UserRegisterResponseDto;
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
    public ApiResponse signup(RegisterUserDto data) {
        ApiResponse response = new ApiResponse();
        try {
            if (userRepository.findByEmail(data.getEmail()).isPresent()) {
                throw new UserAlreadyExistsException("User with email " + data.getEmail() + " already exists");
            }

            User user = new User();
            user.setName(data.getName());
            user.setEmail(data.getEmail());
            user.setPassword(passwordEncoder.encode(data.getPassword()));

            User createdUser = userRepository.save(user);

            UserRegisterResponseDto responseUser = new UserRegisterResponseDto();
            responseUser.setId(String.valueOf(createdUser.getId()));
            responseUser.setName(createdUser.getName());
            responseUser.setEmail(createdUser.getEmail());

            response.setCode(201);
            response.setMessage("User created successfully");
            response.setSuccess(true);
            response.setData(responseUser);

        } catch (UserAlreadyExistsException ex) {
            response.setCode(400);
            response.setMessage(ex.getMessage());
            response.setSuccess(false);
        } catch (Exception err) {
            log.error("Error in creating user: ", err);
            response.setCode(500);
            response.setMessage("Internal server error");
            response.setSuccess(false);
        }
        return response;
    }

    public User authenticate(LoginDto data){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword())
            );
            return userRepository.findByEmail(data.getEmail()).orElseThrow(()->new UsernameNotFoundException("User not found with email: " + data.getEmail()));
        }catch (AuthenticationException err){
            throw new InvalidCredentialsException("Invalid Email/password");
        }
    }
}
