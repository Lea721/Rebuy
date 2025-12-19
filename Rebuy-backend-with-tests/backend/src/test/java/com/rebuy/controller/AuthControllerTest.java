package com.rebuy.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rebuy.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
    }

    @Test
    void registerAndLoginSuccess() throws Exception {
        // Register
        String registerJson = objectMapper.writeValueAsString(
                java.util.Map.of(
                        "email", "mvcuser@example.com",
                        "password", "StrongPass1!",
                        "phone", "0600000001",
                        "name", "Test User"
                )
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mvcuser@example.com"));

        // Login
        String loginJson = objectMapper.writeValueAsString(
                java.util.Map.of(
                        "email", "mvcuser@example.com",
                        "password", "StrongPass1!"
                )
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("mvcuser@example.com"));
    }

    @Test
    void loginWithInvalidEmailFormatShouldReturn400() throws Exception {
        String loginJson = objectMapper.writeValueAsString(
                java.util.Map.of(
                        "email", "invalidemail.com",
                        "password", "StrongPass1!"
                )
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email.type").value("invalid_format"));
    }

    @Test
    void loginWithWrongPasswordShouldReturn401() throws Exception {
        // Register first
        String registerJson = objectMapper.writeValueAsString(
                java.util.Map.of(
                        "email", "mvcuser2@example.com",
                        "password", "StrongPass1!",
                        "phone", "0600000002",
                        "name", "Test User"
                )
        );

        // ADD THIS MISSING LINE - the mockMvc call was incomplete
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk());

        // Attempt login with wrong password
        String loginJson = objectMapper.writeValueAsString(
                java.util.Map.of(
                        "email", "mvcuser2@example.com",
                        "password", "WrongPass!"
                )
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid email or password"));
    }
}
