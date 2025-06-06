package com.wzu.exam.service;

import com.wzu.exam.model.Exam;
import com.wzu.exam.repository.ExamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ExamService {

    @Autowired
    private ExamRepository examRepository;

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    @Transactional
    public Exam createExam(Exam exam) {
        exam.setIsActive(true);
        return examRepository.save(exam);
    }

    @Transactional
    public Exam updateExam(Exam exam) {
        if (!examRepository.existsById(exam.getId())) {
            throw new EntityNotFoundException("Exam not found with id: " + exam.getId());
        }
        return examRepository.save(exam);
    }

    @Transactional
    public void deleteExam(Long id) {
        if (!examRepository.existsById(id)) {
            throw new EntityNotFoundException("Exam not found with id: " + id);
        }
        examRepository.deleteById(id);
    }

    @Transactional
    public Exam updateExamStatus(Long id, boolean isActive) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exam not found with id: " + id));
        exam.setIsActive(isActive);
        return examRepository.save(exam);
    }
} 