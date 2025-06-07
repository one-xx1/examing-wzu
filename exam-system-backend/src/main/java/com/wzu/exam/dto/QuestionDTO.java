package com.wzu.exam.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    private Long id;
    private Long examId;
    private String content;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private List<String> options; // 选项数组，用于前端显示
    private String answer;
    private Integer score;
}