package com.wzu.exam.service.impl;

import com.wzu.exam.dto.UserDTO;
import com.wzu.exam.model.User;
import com.wzu.exam.repository.UserRepository;
import com.wzu.exam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User createUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());
        user.setName(userDTO.getName());
        user.setGender(userDTO.getGender());
        user.setClassName(userDTO.getClassName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setActive(userDTO.isActive());
        
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // 不更新用户名，因为它是唯一标识
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        if (userDTO.getRole() != null && !userDTO.getRole().isEmpty()) {
            user.setRole(userDTO.getRole());
        }
        
        user.setName(userDTO.getName());
        user.setGender(userDTO.getGender());
        user.setClassName(userDTO.getClassName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setActive(userDTO.isActive());
        
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Page<User> getUsersByRole(String role, Pageable pageable) {
        return userRepository.findByRole(role, pageable);
    }

    @Override
    public Page<User> getUsersByRoleAndUsername(String role, String username, Pageable pageable) {
        return userRepository.findByRoleAndUsernameContaining(role, username, pageable);
    }

    @Override
    public Page<User> getUsersByRoleAndName(String role, String name, Pageable pageable) {
        return userRepository.findByRoleAndNameContaining(role, name, pageable);
    }

    @Override
    public Page<User> getUsersByRoleAndUsernameAndName(String role, String username, String name, Pageable pageable) {
        return userRepository.findByRoleAndUsernameContainingAndNameContaining(role, username, name, pageable);
    }

    @Override
    @Transactional
    public void resetPassword(Long id, String defaultPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setPassword(passwordEncoder.encode(defaultPassword));
        userRepository.save(user);
    }
}
