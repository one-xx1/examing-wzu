package com.wzu.exam.dto;

import lombok.Data;
 
@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String name;
    private String gender;
    private String className;
    private String email;
    private String phone;
}