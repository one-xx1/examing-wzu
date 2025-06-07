package com.wzu.exam.controller;

import com.wzu.exam.dto.ExamResultDTO;
import com.wzu.exam.service.AdminExamResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/exams")
@PreAuthorize("hasRole('ADMIN')")
public class AdminExamResultController {

    private final AdminExamResultService adminExamResultService;

    // 获取所有考试成绩（分页）
    @GetMapping("/results")
    public ResponseEntity<Page<ExamResultDTO>> getAllResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) String studentName) {
        
        Page<ExamResultDTO> results = adminExamResultService.getAllResults(
                PageRequest.of(page, size), examId, studentName);
        return ResponseEntity.ok(results);
    }

    // 获取特定考试的成绩
    @GetMapping("/{examId}/results")
    public ResponseEntity<Page<ExamResultDTO>> getResultsByExam(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<ExamResultDTO> results = adminExamResultService.getResultsByExam(
                examId, PageRequest.of(page, size));
        return ResponseEntity.ok(results);
    }

    // 获取成绩详情
    @GetMapping("/results/{resultId}/detail")
    public ResponseEntity<ExamResultDTO> getResultDetail(@PathVariable Long resultId) {
        ExamResultDTO result = adminExamResultService.getResultDetail(resultId);
        return ResponseEntity.ok(result);
    }

    // 删除成绩记录
    @DeleteMapping("/results/{resultId}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long resultId) {
        adminExamResultService.deleteResult(resultId);
        return ResponseEntity.ok().build();
    }

    // 获取考试统计信息
    @GetMapping("/{examId}/statistics")
    public ResponseEntity<?> getExamStatistics(@PathVariable Long examId) {
        // 这里可以返回考试的统计信息，如平均分、最高分、最低分等
        return ResponseEntity.ok().build();
    }
}
