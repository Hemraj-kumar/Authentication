package dev.hemraj.jwtauthentication.Controller;

import dev.hemraj.jwtauthentication.Model.User;
import dev.hemraj.jwtauthentication.Service.UserService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api")
@RestController
public class UserController {
    private final UserService  userService;
    public UserController(UserService userService){
        this.userService = userService;
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list-users")
    public List<User> getAllUser(){
        List<User> users = userService.allUsers();
        return users;
    }
}
