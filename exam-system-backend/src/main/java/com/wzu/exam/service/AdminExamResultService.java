package com.wzu.exam.service;

import com.wzu.exam.dto.ExamResultDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminExamResultService {
    
    // 获取所有考试成绩（分页，支持筛选）
    Page<ExamResultDTO> getAllResults(Pageable pageable, Long examId, String studentName);
    
    // 获取特定考试的成绩
    Page<ExamResultDTO> getResultsByExam(Long examId, Pageable pageable);
    
    // 获取成绩详情
    ExamResultDTO getResultDetail(Long resultId);
    
    // 删除成绩记录
    void deleteResult(Long resultId);
}
