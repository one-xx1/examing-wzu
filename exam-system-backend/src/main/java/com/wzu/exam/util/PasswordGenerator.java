package com.wzu.exam.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123456";
        String encodedPassword = encoder.encode(password);
        System.out.println("Original password: " + password);
        System.out.println("Encoded password: " + encodedPassword);
        
        // 验证密码
        boolean matches = encoder.matches(password, encodedPassword);
        System.out.println("Password matches: " + matches);
        
        // 验证数据库中的密码
        String dbPassword = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi";
        boolean dbMatches = encoder.matches(password, dbPassword);
        System.out.println("Database password matches: " + dbMatches);
    }
} 