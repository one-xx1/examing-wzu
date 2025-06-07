package com.wzu.exam.controller;

import com.wzu.exam.dto.LoginRequest;
import com.wzu.exam.dto.LoginResponse;
import com.wzu.exam.dto.RegisterRequest;
import com.wzu.exam.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        log.info("收到登录请求: username={}", loginRequest.getUsername());
        try {
        LoginResponse response = authService.login(loginRequest);
            log.info("登录成功: username={}, role={}", loginRequest.getUsername(), response.getRole());
        return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("登录失败: username={}, error={}", loginRequest.getUsername(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest registerRequest) {
        log.info("收到注册请求: username={}", registerRequest.getUsername());
        try {
            authService.register(registerRequest);
            log.info("注册成功: username={}", registerRequest.getUsername());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("注册失败: username={}, error={}", registerRequest.getUsername(), e.getMessage());
            throw e;
        }
    }
} 