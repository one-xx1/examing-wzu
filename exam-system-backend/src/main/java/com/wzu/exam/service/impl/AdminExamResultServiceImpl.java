package com.wzu.exam.service.impl;

import com.wzu.exam.dto.ExamResultDTO;
import com.wzu.exam.dto.StudentAnswerDTO;
import com.wzu.exam.model.ExamResult;
import com.wzu.exam.model.StudentAnswer;
import com.wzu.exam.repository.ExamResultRepository;
import com.wzu.exam.repository.StudentAnswerRepository;
import com.wzu.exam.service.AdminExamResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminExamResultServiceImpl implements AdminExamResultService {

    private final ExamResultRepository examResultRepository;
    private final StudentAnswerRepository studentAnswerRepository;

    @Override
    public Page<ExamResultDTO> getAllResults(Pageable pageable, Long examId, String studentName) {
        Page<ExamResult> resultPage;
        
        if (examId != null && studentName != null && !studentName.trim().isEmpty()) {
            // 按考试ID和学生姓名筛选
            resultPage = examResultRepository.findByExamIdAndUserNameContaining(examId, studentName, pageable);
        } else if (examId != null) {
            // 按考试ID筛选
            resultPage = examResultRepository.findByExamId(examId, pageable);
        } else if (studentName != null && !studentName.trim().isEmpty()) {
            // 按学生姓名筛选
            resultPage = examResultRepository.findByUserNameContaining(studentName, pageable);
        } else {
            // 获取所有成绩
            resultPage = examResultRepository.findAll(pageable);
        }
        
        List<ExamResultDTO> resultDTOs = resultPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(resultDTOs, pageable, resultPage.getTotalElements());
    }

    @Override
    public Page<ExamResultDTO> getResultsByExam(Long examId, Pageable pageable) {
        Page<ExamResult> resultPage = examResultRepository.findByExamId(examId, pageable);
        
        List<ExamResultDTO> resultDTOs = resultPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageImpl<>(resultDTOs, pageable, resultPage.getTotalElements());
    }

    @Override
    public ExamResultDTO getResultDetail(Long resultId) {
        ExamResult result = examResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("考试结果不存在"));
        
        // 获取学生答案详情
        List<StudentAnswer> answers = studentAnswerRepository.findByUserAndExam(result.getUser(), result.getExam());
        
        ExamResultDTO dto = convertToDTO(result);
        
        // 添加答题详情
        List<StudentAnswerDTO> answerDTOs = answers.stream()
                .map(this::convertToStudentAnswerDTO)
                .collect(Collectors.toList());
        
        dto.setAnswers(answerDTOs);
        
        return dto;
    }

    @Override
    @Transactional
    public void deleteResult(Long resultId) {
        ExamResult result = examResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("考试结果不存在"));
        
        // 删除相关的学生答案
        studentAnswerRepository.deleteByUserAndExam(result.getUser(), result.getExam());
        
        // 删除考试结果
        examResultRepository.delete(result);
    }

    // 辅助方法 - 转换为DTO
    private ExamResultDTO convertToDTO(ExamResult result) {
        ExamResultDTO dto = new ExamResultDTO();
        dto.setId(result.getId());
        dto.setExamId(result.getExam().getId());
        dto.setExamTitle(result.getExam().getTitle());
        dto.setStudentName(result.getUser().getName() != null ? result.getUser().getName() : result.getUser().getUsername());
        dto.setScore(result.getScore());
        dto.setTotalScore(100); // 满分100
        dto.setDuration(result.getDuration());
        dto.setSubmittedAt(result.getCreatedAt());
        return dto;
    }

    // 辅助方法 - 转换学生答案为DTO
    private StudentAnswerDTO convertToStudentAnswerDTO(StudentAnswer answer) {
        StudentAnswerDTO dto = new StudentAnswerDTO();
        dto.setQuestionId(answer.getQuestion().getId());
        dto.setQuestion(answer.getQuestion().getContent());
        dto.setOptionA(answer.getQuestion().getOptionA());
        dto.setOptionB(answer.getQuestion().getOptionB());
        dto.setOptionC(answer.getQuestion().getOptionC());
        dto.setOptionD(answer.getQuestion().getOptionD());
        dto.setSelectedAnswer(answer.getSelectedAnswer());
        dto.setCorrectAnswer(answer.getQuestion().getAnswer());
        dto.setIsCorrect(answer.getIsCorrect());
        return dto;
    }
}
