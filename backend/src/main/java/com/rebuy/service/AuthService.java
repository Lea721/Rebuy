package com.rebuy.service;

import com.rebuy.controller.dto.LoginRequest;
import com.rebuy.controller.dto.RegisterRequest;
import com.rebuy.controller.dto.UserResponse;
import com.rebuy.entity.User;
import com.rebuy.exception.AuthenticationException;
import com.rebuy.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Password policy: min 8 chars, at least one upper, one lower, one digit and one special
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$");

    // Simple strict email pattern: must contain '@' and a domain part
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ============ REGISTER ============
    public User register(RegisterRequest request) {

        String email = request.getEmail();
        String password = request.getPassword();
        String phone = request.getPhone();

        // ensure email matches a strict pattern (defensive: DTO also validates this)
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new com.rebuy.exception.FieldValidationException(
                java.util.Map.of("email", "Email must be a valid address and contain '@' and a domain"),
                java.util.Map.of("email", "invalid_format")
            );
        }

        // email and password validation are also handled by DTO annotations
        if (userRepository.existsByEmail(email)) {
            throw new com.rebuy.exception.FieldValidationException(
                    java.util.Map.of("email", "Email already in use"),
                    java.util.Map.of("email", "duplicate")
            );
        }

        // validate phone uniqueness when provided
        if (phone != null && !phone.isBlank()) {
            if (userRepository.existsByPhone(phone)) {
                throw new com.rebuy.exception.FieldValidationException(
                        java.util.Map.of("phone", "Phone already used"),
                        java.util.Map.of("phone", "duplicate")
                );
            }
        }

        // validate password strength at service layer as well (length + char classes)
        if (password == null || password.length() < 8) {
            throw new com.rebuy.exception.FieldValidationException(
                    java.util.Map.of("password", "Password must be at least 8 characters"),
                    java.util.Map.of("password", "size")
            );
        }

        // pattern already enforces length, but check character classes separately for clearer message
        Pattern charClassPattern = Pattern.compile("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).*");
        if (!charClassPattern.matcher(password).matches()) {
            throw new com.rebuy.exception.FieldValidationException(
                    java.util.Map.of("password", "Password must include uppercase, lowercase, digit and special character"),
                    java.util.Map.of("password", "invalid_format")
            );
        }

        User user = new User();
        user.setEmail(email);
        user.setName(request.getName());
        user.setPhone(phone);
        user.setCity(request.getCity());
        user.setShippingAddress(request.getShippingAddress());

        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    // ============ LOGIN ============
    public User login(LoginRequest request) {

        String email = request.getEmail();

        // validate email format before authenticating
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new com.rebuy.exception.FieldValidationException(
                java.util.Map.of("email", "Email must be a valid address and contain '@' and a domain"),
                java.util.Map.of("email", "invalid_format")
            );
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid email or password");
        }

        return user;
    }

    // ============ Convert User → UserResponse ============
    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setCity(user.getCity());
        response.setShippingAddress(user.getShippingAddress());
        response.setProfileImageUrl(user.getProfileImageUrl());

        return response;
    }

    private boolean isStrongPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}

