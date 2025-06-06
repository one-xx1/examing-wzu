package com.wzu.exam.service.impl;

import com.wzu.exam.dto.QuestionDTO;
import com.wzu.exam.model.Exam;
import com.wzu.exam.model.Question;
import com.wzu.exam.repository.ExamRepository;
import com.wzu.exam.repository.QuestionRepository;
import com.wzu.exam.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;

    @Override
    @Transactional
    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        if (questionDTO.getExamId() == null) {
            throw new IllegalArgumentException("Exam ID is required");
        }
        
        Exam exam = examRepository.findById(questionDTO.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found with id: " + questionDTO.getExamId()));
        
        Question question = new Question();
        question.setContent(questionDTO.getContent());
        question.setOptionA(questionDTO.getOptionA());
        question.setOptionB(questionDTO.getOptionB());
        question.setOptionC(questionDTO.getOptionC());
        question.setOptionD(questionDTO.getOptionD());
        question.setAnswer(questionDTO.getAnswer());
        question.setScore(questionDTO.getScore() != null ? questionDTO.getScore() : 1);
        question.setExam(exam);

        Question savedQuestion = questionRepository.save(question);
        return convertToDTO(savedQuestion);
    }

    private QuestionDTO convertToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setContent(question.getContent());
        dto.setOptionA(question.getOptionA());
        dto.setOptionB(question.getOptionB());
        dto.setOptionC(question.getOptionC());
        dto.setOptionD(question.getOptionD());
        dto.setAnswer(question.getAnswer());
        
        if (question.getExam() != null) {
            dto.setExamId(question.getExam().getId());
        }
        
        return dto;
    }

    @Override
    @Transactional
    public QuestionDTO updateQuestion(Long id, QuestionDTO questionDTO) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
        question.setContent(questionDTO.getContent());
        question.setOptionA(questionDTO.getOptionA());
        question.setOptionB(questionDTO.getOptionB());
        question.setOptionC(questionDTO.getOptionC());
        question.setOptionD(questionDTO.getOptionD());
        question.setAnswer(questionDTO.getAnswer());

        Question updatedQuestion = questionRepository.save(question);
        return convertToDTO(updatedQuestion);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }

    @Override
    public QuestionDTO getQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        return convertToDTO(question);
    }

    @Override
    public List<QuestionDTO> getAllQuestions() {
        return questionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<QuestionDTO> getQuestionsByExamId(Long examId) {
        return questionRepository.findByExamId(examId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<QuestionDTO> getAllQuestions(Pageable pageable) {
        return questionRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    public Page<QuestionDTO> getQuestionsByExamId(Long examId, Pageable pageable) {
        return questionRepository.findByExamId(examId, pageable)
                .map(this::convertToDTO);
    }
    
    @Override
    public Page<QuestionDTO> getQuestionsByType(String type, Pageable pageable) {
        // 目前系统中没有实现题目类型区分，所有题目都是单选题
        // 如果将来需要按类型查询，需要在Question实体中添加type字段
        // 暂时返回所有题目
        return getAllQuestions(pageable);
    }
} 