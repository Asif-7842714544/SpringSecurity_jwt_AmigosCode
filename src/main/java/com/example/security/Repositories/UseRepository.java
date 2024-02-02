package com.example.security.Repositories;

import com.example.security.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UseRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);
}
