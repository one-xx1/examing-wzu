package com.wzu.exam.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "option_a", nullable = false)
    private String optionA;

    @Column(name = "option_b", nullable = false)
    private String optionB;

    @Column(name = "option_c", nullable = false)
    private String optionC;

    @Column(name = "option_d", nullable = false)
    private String optionD;

    @Column(nullable = false)
    private String answer;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private String type = "SINGLE_CHOICE"; // 设置默认值为单选题

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 