package com.rebuy.repository;

import com.rebuy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// This interface gives CRUD operations for the User entity.
// Spring Data JPA generates all queries automatically.
public interface UserRepository extends JpaRepository<User, Long> {

    // Check if an email already exists in the database
    boolean existsByEmail(String email);

    // Find a user by email (used for login)
    Optional<User> findByEmail(String email);

    // Check if a phone number already exists
    boolean existsByPhone(String phone);

    // Find a user by phone (used to validate uniqueness on update)
    Optional<User> findByPhone(String phone);
}
