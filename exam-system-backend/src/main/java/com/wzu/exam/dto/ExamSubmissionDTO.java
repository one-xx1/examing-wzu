package com.wzu.exam.dto;

import lombok.Data;
import java.util.List;

@Data
public class ExamSubmissionDTO {
    private Long examId;
    private List<QuestionAnswerDTO> answers;
    private Integer duration; // 考试用时（秒）
}
