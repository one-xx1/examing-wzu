package com.wzu.exam.service;

import com.wzu.exam.dto.ExamDetailDTO;
import com.wzu.exam.dto.ExamResultDTO;
import com.wzu.exam.dto.ExamSubmissionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentExamService {
    // 获取学生可参加的考试列表（分页）
    Page<ExamDetailDTO> getAvailableExams(Pageable pageable, String username);

    // 获取考试详情（包含题目）
    ExamDetailDTO getExamById(Long examId, String username);

    // 检查学生是否已经参加过该考试
    boolean hasAttemptedExam(Long examId, String username);

    // 提交考试答案
    ExamResultDTO submitExam(ExamSubmissionDTO submissionDTO, String username);

    // 获取学生的考试结果（分页）
    Page<ExamResultDTO> getStudentResults(Pageable pageable, String username);

    // 获取特定考试结果
    ExamResultDTO getExamResultById(Long resultId, String username);
}
