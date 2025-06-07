package com.wzu.exam.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "system_settings")
public class SystemSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "system_name")
    private String systemName;

    @Column(name = "system_description", length = 500)
    private String systemDescription;

    @Column(name = "admin_email")
    private String adminEmail;

    @Column(name = "default_exam_duration")
    private Integer defaultExamDuration;

    @Column(name = "default_pass_score")
    private Integer defaultPassScore;
}
