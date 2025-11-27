package com.rebuy.controller;

import com.rebuy.controller.dto.LoginRequest;
import com.rebuy.controller.dto.RegisterRequest;
import com.rebuy.controller.dto.UserResponse;
import com.rebuy.entity.User;
import com.rebuy.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ============ REGISTER ============
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.ok(authService.toResponse(user));
    }

    // ============ LOGIN ============
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request);
        return ResponseEntity.ok(authService.toResponse(user));
    }
}
