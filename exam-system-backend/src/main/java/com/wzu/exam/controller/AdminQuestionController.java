package com.wzu.exam.controller;

import com.wzu.exam.dto.QuestionDTO;
import com.wzu.exam.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/questions")
public class AdminQuestionController {
    private static final Logger logger = LoggerFactory.getLogger(AdminQuestionController.class);

    @Autowired
    private QuestionService questionService;

    @GetMapping
    public ResponseEntity<Page<QuestionDTO>> getQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type) {
        logger.info("Getting questions page {} size {}, type: {}", page, size, type);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<QuestionDTO> questions;
            
            if (type != null && !type.isEmpty()) {
                questions = questionService.getQuestionsByType(type, pageable);
            } else {
                questions = questionService.getAllQuestions(pageable);
            }
            
            logger.info("Retrieved {} questions", questions.getTotalElements());
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            logger.error("Error getting questions", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestion(@PathVariable Long id) {
        logger.info("Getting question with id: {}", id);
        try {
            QuestionDTO question = questionService.getQuestion(id);
            logger.info("Question retrieved successfully: {}", question);
            return ResponseEntity.ok(question);
        } catch (Exception e) {
            logger.error("Error getting question", e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<QuestionDTO> createQuestion(@RequestBody QuestionDTO questionDTO) {
        logger.info("Creating new question: {}", questionDTO);
        try {
            QuestionDTO created = questionService.createQuestion(questionDTO);
            logger.info("Question created successfully: {}", created);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            logger.error("Error creating question", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionDTO> updateQuestion(@PathVariable Long id, @RequestBody QuestionDTO questionDTO) {
        logger.info("Updating question with id {}: {}", id, questionDTO);
        try {
            QuestionDTO updated = questionService.updateQuestion(id, questionDTO);
            logger.info("Question updated successfully: {}", updated);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating question", e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        logger.info("Deleting question with id: {}", id);
        try {
            questionService.deleteQuestion(id);
            logger.info("Question deleted successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting question", e);
            throw e;
        }
    }
    
    @GetMapping("/exam/{examId}")
    public ResponseEntity<Page<QuestionDTO>> getQuestionsByExamId(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Getting questions for exam with id: {}, page: {}, size: {}", examId, page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<QuestionDTO> questions = questionService.getQuestionsByExamId(examId, pageable);
            logger.info("Retrieved {} questions for exam {}", questions.getTotalElements(), examId);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            logger.error("Error getting questions for exam {}", examId, e);
            throw e;
        }
    }
}

