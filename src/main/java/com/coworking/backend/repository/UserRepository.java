package com.coworking.backend.repository;

import com.coworking.backend.model.User;
import com.coworking.backend.model.User.UserType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    Boolean existsByPhone(String phone);
    List<User> findByType(UserType type);
}