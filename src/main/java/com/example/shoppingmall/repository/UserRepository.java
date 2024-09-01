package com.example.shoppingmall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.shoppingmall.model.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String username);
    User findByEmail(String email);
    Optional<User> findByNameAndPassword(String username, String password);
    Optional<User> findByEmailAndPassword(String email, String password);
    Boolean existsByEmail(String email);
}
