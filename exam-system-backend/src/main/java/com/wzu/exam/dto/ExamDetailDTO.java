package com.wzu.exam.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamDetailDTO {
    private Long id;
    private String title;
    private String description;
    private String subject;
    private Integer duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private Boolean isActive;
    private String status; // NOT_STARTED, IN_PROGRESS, COMPLETED, EXPIRED
    private List<QuestionDTO> questions;
}
