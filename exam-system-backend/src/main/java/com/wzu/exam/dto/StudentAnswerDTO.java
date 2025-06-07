package com.wzu.exam.dto;

import lombok.Data;

@Data
public class StudentAnswerDTO {
    private Long questionId;
    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String selectedAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
}
