package com.example.bankingproject.repository;

import com.example.bankingproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);
    Boolean existsByAccountNumber(String accountNumber);
    User findByAccountNumber(String accountNumber);
}
