package dev.hemraj.kafka_001.service.authentication;

import dev.hemraj.kafka_001.exception.InvalidCredentialsException;
import dev.hemraj.kafka_001.exception.UserAlreadyExistsException;
import dev.hemraj.kafka_001.model.ApiResponse;
import dev.hemraj.kafka_001.model.ErrorBO;
import dev.hemraj.kafka_001.model.User;
import dev.hemraj.kafka_001.model.dto.LoginDto;
import dev.hemraj.kafka_001.model.dto.LoginReponseDto;
import dev.hemraj.kafka_001.model.dto.RegisterUserDto;
import dev.hemraj.kafka_001.model.dto.UserRegisterResponseDto;
import dev.hemraj.kafka_001.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.table.TableRowSorter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
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
            user.setMobile(data.getMobile());

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
    public ApiResponse loginUser(LoginDto data){
        ApiResponse response = new ApiResponse();
        List<ErrorBO> errorBOList  = new ArrayList<>();
        try{
            User user = authenticate(data);
            if(user!=null){
                LoginReponseDto loginResponse= new LoginReponseDto();
                String generatedToken = jwtService.generateToken(user);
                if(generatedToken!=null && !generatedToken.isEmpty()){
                    loginResponse.setToken(generatedToken);
                    loginResponse.setId(user.getId());
                    response.setData(loginResponse);
                    response.setCode(200);
                    response.setMessage("User logged in successfully");
                }else{
                   errorBOList.add(new ErrorBO(500,"","Error generating token for email : "+data.getEmail()));
                }
            }else{
                errorBOList.add(new ErrorBO(500,"","Error , Unable to verify user with email : "+ data.getEmail()));
            }
        }catch (Exception err){
            log.error("Error validating user with email : {}", data.getEmail(),err);
            response.setCode(500);
            response.setSuccess(Boolean.FALSE);
        }
        response.setErrorBOList(errorBOList);
        return response;
    }
}
