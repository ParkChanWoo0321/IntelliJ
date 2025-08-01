package com.example.hackerthon.Login.repository;

import com.example.hackerthon.Login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
