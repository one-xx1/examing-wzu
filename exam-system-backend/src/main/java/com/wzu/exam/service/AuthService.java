package com.wzu.exam.service;

import com.wzu.exam.dto.LoginRequest;
import com.wzu.exam.dto.LoginResponse;
import com.wzu.exam.dto.RegisterRequest;
import com.wzu.exam.model.User;
import com.wzu.exam.repository.UserRepository;
import com.wzu.exam.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest) {
        log.info("开始处理登录请求: username={}, role={}", loginRequest.getUsername(), loginRequest.getRole());
        try {
            // 获取用户信息
            User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

            // 验证角色
            if (!user.getRole().equals(loginRequest.getRole())) {
                log.error("角色不匹配: expected={}, actual={}", loginRequest.getRole(), user.getRole());
                throw new BadCredentialsException("用户角色不匹配");
            }

            // 验证用户名和密码
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // 生成token
            String token = tokenProvider.generateToken(authentication);

            log.info("用户验证成功: username={}, role={}", user.getUsername(), user.getRole());

            // 返回登录响应
            return LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .build();
        } catch (AuthenticationException e) {
            log.error("用户验证失败: username={}, error={}", loginRequest.getUsername(), e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void register(RegisterRequest registerRequest) {
        log.info("开始处理注册请求: username={}", registerRequest.getUsername());
        
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setName(registerRequest.getName());
        user.setRole("STUDENT"); // 注册的用户默认为学生角色
        user.setActive(true);

        // 保存用户
        userRepository.save(user);
        log.info("用户注册成功: username={}", user.getUsername());
    }
} 