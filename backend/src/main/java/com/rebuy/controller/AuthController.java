package com.rebuy.controller;

import com.rebuy.controller.dto.LoginRequest;
import com.rebuy.controller.dto.RegisterRequest;
import com.rebuy.entity.User;
import com.rebuy.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);
        // you might later create a DTO to hide password
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest request) {
        User user = authService.login(request);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }
}
