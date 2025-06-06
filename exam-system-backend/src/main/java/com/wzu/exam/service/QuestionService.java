package com.wzu.exam.service;

import com.wzu.exam.dto.QuestionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {
    QuestionDTO createQuestion(QuestionDTO questionDTO);
    QuestionDTO updateQuestion(Long id, QuestionDTO questionDTO);
    void deleteQuestion(Long id);
    QuestionDTO getQuestion(Long id);
    
    // 不分页的方法 - 保留兼容性
    List<QuestionDTO> getAllQuestions();
    List<QuestionDTO> getQuestionsByExamId(Long examId);
    
    // 分页的方法
    Page<QuestionDTO> getAllQuestions(Pageable pageable);
    Page<QuestionDTO> getQuestionsByExamId(Long examId, Pageable pageable);
    Page<QuestionDTO> getQuestionsByType(String type, Pageable pageable);
}