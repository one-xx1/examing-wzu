package com.wzu.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String gender;
    private String className;
    private String email;
    private String phone;
    private String role;
    private boolean active;
}
