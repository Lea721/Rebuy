package com.rebuy.service;

import com.rebuy.controller.dto.RegisterRequest;
import com.rebuy.controller.dto.LoginRequest;
import com.rebuy.entity.User;
import com.rebuy.repository.UserRepository;
import com.rebuy.exception.FieldValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void register_withInvalidEmail_shouldThrowFieldValidation() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Test");
        req.setEmail("noatsymbol.com");
        req.setPassword("Str0ngP@ss1!");
        req.setPhone("+33100000001");

        FieldValidationException ex = Assertions.assertThrows(FieldValidationException.class, () -> {
            authService.register(req);
        });
        Assertions.assertTrue(ex.getErrors().containsKey("email"));
    }

    @Test
    void register_withShortPassword_shouldThrowFieldValidation() {
        RegisterRequest req = new RegisterRequest();
        req.setName("Test");
        req.setEmail("t@example.com");
        req.setPassword("short");
        req.setPhone("+33100000002");

        FieldValidationException ex = Assertions.assertThrows(FieldValidationException.class, () -> {
            authService.register(req);
        });
        Assertions.assertTrue(ex.getErrors().containsKey("password"));
    }

    @Test
    void register_duplicatePhone_shouldThrowFieldValidation() {
        RegisterRequest r1 = new RegisterRequest();
        r1.setName("A");
        r1.setEmail("a1@example.com");
        r1.setPassword("Str0ngP@ss1!");
        r1.setPhone("+33100000003");

        User u = authService.register(r1);
        Assertions.assertNotNull(u.getId());

        RegisterRequest r2 = new RegisterRequest();
        r2.setName("B");
        r2.setEmail("b1@example.com");
        r2.setPassword("Str0ngP@ss1!");
        r2.setPhone("+33100000003");

        FieldValidationException ex = Assertions.assertThrows(FieldValidationException.class, () -> {
            authService.register(r2);
        });
        Assertions.assertTrue(ex.getErrors().containsKey("phone"));
    }

    @Test
    void register_and_login_success() {  // ADD THIS METHOD SIGNATURE
        RegisterRequest req = new RegisterRequest();
        req.setName("Test2");
        req.setEmail("t2@example.com");
        req.setPassword("Str0ngP@ss1!");
        req.setPhone("+33100000004");

        User u = authService.register(req);
        Assertions.assertNotNull(u.getId());

        LoginRequest login = new LoginRequest();
        login.setEmail(req.getEmail());
        login.setPassword(req.getPassword());

        User logged = authService.login(login);
        Assertions.assertEquals(u.getEmail(), logged.getEmail());
    }
}
