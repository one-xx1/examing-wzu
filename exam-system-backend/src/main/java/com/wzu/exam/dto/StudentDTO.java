package com.wzu.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String gender;
    private String className;
    private String email;
    private String phone;
    private boolean active;
}
