package com.rebuy.backend.controller;

import com.rebuy.backend.model.User;
import com.rebuy.backend.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest req) {
        return userService.login(req.getEmail(), req.getPassword());
    }
}

@Data
@AllArgsConstructor
class LoginRequest {
    private String email;
    private String password;
}
