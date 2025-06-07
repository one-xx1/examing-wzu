package com.wzu.exam.controller;

import com.wzu.exam.dto.ExamDetailDTO;
import com.wzu.exam.dto.ExamResultDTO;
import com.wzu.exam.dto.ExamSubmissionDTO;
import com.wzu.exam.service.StudentExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentExamController {

    private final StudentExamService studentExamService;

    @GetMapping("/exams")
    public ResponseEntity<Page<ExamDetailDTO>> getAvailableExams(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Page<ExamDetailDTO> exams = studentExamService.getAvailableExams(
                PageRequest.of(page, size), userDetails.getUsername());
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/exams/{examId}")
    public ResponseEntity<ExamDetailDTO> getExamById(
            @PathVariable Long examId,
            @AuthenticationPrincipal UserDetails userDetails) {
        // 检查学生是否已经参加过该考试
        if (studentExamService.hasAttemptedExam(examId, userDetails.getUsername())) {
            return ResponseEntity.badRequest().build();
        }

        ExamDetailDTO exam = studentExamService.getExamById(examId, userDetails.getUsername());
        return ResponseEntity.ok(exam);
    }

    @GetMapping("/exams/{examId}/check")
    public ResponseEntity<Map<String, Boolean>> checkExamAttempt(
            @PathVariable Long examId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean hasAttempted = studentExamService.hasAttemptedExam(examId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("hasAttempted", hasAttempted));
    }

    @PostMapping("/exams/submit")
    public ResponseEntity<ExamResultDTO> submitExam(
            @RequestBody ExamSubmissionDTO submissionDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        ExamResultDTO result = studentExamService.submitExam(submissionDTO, userDetails.getUsername());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/results")
    public ResponseEntity<Page<ExamResultDTO>> getStudentResults(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Page<ExamResultDTO> results = studentExamService.getStudentResults(
                PageRequest.of(page, size), userDetails.getUsername());
        return ResponseEntity.ok(results);
    }

    @GetMapping("/results/{resultId}")
    public ResponseEntity<ExamResultDTO> getExamResultById(
            @PathVariable Long resultId,
            @AuthenticationPrincipal UserDetails userDetails) {
        ExamResultDTO result = studentExamService.getExamResultById(resultId, userDetails.getUsername());
        return ResponseEntity.ok(result);
    }
}
