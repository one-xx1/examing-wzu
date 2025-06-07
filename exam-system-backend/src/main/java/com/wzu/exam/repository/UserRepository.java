package com.wzu.exam.repository;

import com.wzu.exam.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    
    // 学生管理相关查询方法
    Page<User> findByRole(String role, Pageable pageable);
    Page<User> findByRoleAndUsernameContaining(String role, String username, Pageable pageable);
    Page<User> findByRoleAndNameContaining(String role, String name, Pageable pageable);
    Page<User> findByRoleAndUsernameContainingAndNameContaining(String role, String username, String name, Pageable pageable);
}