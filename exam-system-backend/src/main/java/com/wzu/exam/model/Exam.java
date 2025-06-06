package com.wzu.exam.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "exam")
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column
    private String subject;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    // 为了兼容前端，添加 duration 属性
    public Integer getDuration() {
        return this.durationMinutes;
    }

    public void setDuration(Integer duration) {
        this.durationMinutes = duration;
    }

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "passing_score")
    private Integer passingScore;

    @Column(name = "is_active")
    private Boolean isActive = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonManagedReference
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    private List<Question> questions;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 