package com.example.makanmaniabe.repository;

import java.util.Optional;
import java.util.UUID;

import com.example.makanmaniabe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}