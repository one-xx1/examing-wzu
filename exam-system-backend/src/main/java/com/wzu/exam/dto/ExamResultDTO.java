package com.wzu.exam.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExamResultDTO {
    private Long id;
    private Long examId;
    private String examTitle;
    private String studentName; // 学生姓名
    private Integer score;
    private Integer totalScore;
    private Integer duration; // 考试用时（秒）
    private LocalDateTime submittedAt;
    private List<StudentAnswerDTO> answers;
}
