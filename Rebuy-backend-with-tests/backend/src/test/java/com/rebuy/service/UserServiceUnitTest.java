package com.rebuy.service;

import com.rebuy.controller.dto.UserResponse;
import com.rebuy.controller.dto.UserUpdateRequest;
import com.rebuy.entity.User;
import com.rebuy.exception.FieldValidationException;
import com.rebuy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        User user = new User("test@example.com", "password", "Test User");
        user.setId(1L);

        // FIX: Use setters instead of constructor with arguments
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("test@example.com");
        userResponse.setName("Test User");
        // Other fields can remain null

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(authService.toResponse(user)).thenReturn(userResponse);

        // Act
        UserResponse result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.getUserById(999L));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void updateUser_ShouldUpdateAllFields_WhenValidRequest() {
        // Arrange
        User user = new User("test@example.com", "password", "Test User");
        user.setId(1L);

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setName("Updated Name");
        updateRequest.setPhone("1234567890");
        updateRequest.setCity("New City");
        updateRequest.setShippingAddress("New Address");
        // FIX: Use HTTPS instead of HTTP
        updateRequest.setProfileImageUrl("https://example.com/image.jpg");

        // FIX: Use setters instead of constructor with arguments
        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setId(1L);
        updatedResponse.setEmail("test@example.com");
        updatedResponse.setName("Updated Name");
        updatedResponse.setPhone("1234567890");
        updatedResponse.setCity("New City");
        updatedResponse.setShippingAddress("New Address");
        updatedResponse.setProfileImageUrl("https://example.com/image.jpg");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByPhone("1234567890")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(authService.toResponse(user)).thenReturn(updatedResponse);

        // Act
        UserResponse result = userService.updateUser(1L, updateRequest);

        // Assert
        assertEquals("Updated Name", result.getName());
        assertEquals("1234567890", result.getPhone());
        assertEquals("New City", result.getCity());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser_ShouldThrowException_WhenPhoneAlreadyUsed() {
        // Arrange
        User user = new User("test@example.com", "password", "Test User");
        user.setId(1L);

        User otherUser = new User("other@example.com", "password", "Other User");
        otherUser.setId(2L);

        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setPhone("1234567890");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByPhone("1234567890")).thenReturn(Optional.of(otherUser));

        // Act & Assert
        FieldValidationException exception = assertThrows(FieldValidationException.class,
                () -> userService.updateUser(1L, updateRequest));

        assertTrue(exception.getErrors().containsKey("phone"));
        verify(userRepository, never()).save(any());
    }
}