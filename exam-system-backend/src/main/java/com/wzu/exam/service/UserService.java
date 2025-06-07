package com.wzu.exam.service;

import com.wzu.exam.dto.UserDTO;
import com.wzu.exam.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {
    User createUser(UserDTO userDTO);
    User updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    Optional<User> getUserById(Long id);
    Optional<User> getUserByUsername(String username);
    Page<User> getAllUsers(Pageable pageable);
    Page<User> getUsersByRole(String role, Pageable pageable);
    Page<User> getUsersByRoleAndUsername(String role, String username, Pageable pageable);
    Page<User> getUsersByRoleAndName(String role, String name, Pageable pageable);
    Page<User> getUsersByRoleAndUsernameAndName(String role, String username, String name, Pageable pageable);
    void resetPassword(Long id, String defaultPassword);
}
