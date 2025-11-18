package com.rebuy.service;

import com.rebuy.controller.dto.UserResponse;
import com.rebuy.controller.dto.UserUpdateRequest;
import com.rebuy.entity.User;
import com.rebuy.repository.UserRepository;
import org.springframework.stereotype.Service;

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
    public UserResponse updateUser(Long id, UserUpdateRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // update fields
        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getCity() != null) user.setCity(request.getCity());
        if (request.getShippingAddress() != null) user.setShippingAddress(request.getShippingAddress());
        if (request.getProfileImageUrl() != null) user.setProfileImageUrl(request.getProfileImageUrl());

        userRepository.save(user);

        return authService.toResponse(user);
    }
}
