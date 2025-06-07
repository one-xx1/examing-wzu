package com.wzu.exam.dto;

import lombok.Data;

@Data
public class SystemSettingsDTO {
    private String systemName;
    private String systemDescription;
    private String adminEmail;
    private Integer defaultExamDuration;
    private Integer defaultPassScore;
}
