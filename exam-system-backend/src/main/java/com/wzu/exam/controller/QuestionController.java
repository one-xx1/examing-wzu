package com.wzu.exam.controller;

import com.wzu.exam.dto.QuestionDTO;
import com.wzu.exam.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private QuestionService questionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
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

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<List<QuestionDTO>> getAllQuestions() {
        logger.info("Getting all questions");
        try {
            List<QuestionDTO> questions = questionService.getAllQuestions();
            logger.info("Retrieved {} questions", questions.size());
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            logger.error("Error getting all questions", e);
            throw e;
        }
    }
    
    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByExamId(@PathVariable Long examId) {
        logger.info("Getting questions for exam with id: {}", examId);
        try {
            List<QuestionDTO> questions = questionService.getQuestionsByExamId(examId);
            logger.info("Retrieved {} questions for exam {}", questions.size(), examId);
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            logger.error("Error getting questions for exam {}", examId, e);
            throw e;
        }
    }
}