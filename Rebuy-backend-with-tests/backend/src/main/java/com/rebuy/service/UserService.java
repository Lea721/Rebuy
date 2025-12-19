package com.rebuy.service;

import com.rebuy.controller.dto.UserResponse;
import com.rebuy.controller.dto.UserUpdateRequest;
import com.rebuy.entity.User;
import com.rebuy.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService; // for mapping User → UserResponse

    public UserService(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    // ============ GET USER BY ID ============
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return authService.toResponse(user);
    }

    // ============ UPDATE USER PROFILE ============
// In UserService.java - add the missing return statement
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // update fields
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) {
            String newPhone = request.getPhone();
            Optional<User> existing = userRepository.findByPhone(newPhone);
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new com.rebuy.exception.FieldValidationException(
                        java.util.Map.of("phone", "Phone already used"),
                        java.util.Map.of("phone", "duplicate")
                );
            }
            user.setPhone(newPhone);
        }
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getShippingAddress() != null) user.setShippingAddress(request.getShippingAddress());
        if (request.getProfileImageUrl() != null) user.setProfileImageUrl(request.getProfileImageUrl());

        userRepository.save(user);

        return authService.toResponse(user); // ADD THIS MISSING RETURN
    }
}
