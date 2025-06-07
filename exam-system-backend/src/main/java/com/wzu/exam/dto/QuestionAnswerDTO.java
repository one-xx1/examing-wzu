package com.wzu.exam.dto;

import lombok.Data;

@Data
public class QuestionAnswerDTO {
    private Long questionId;
    private String selectedAnswer;
    private Integer selectedOption; // 选择的选项索引 (0, 1, 2, 3)
}
