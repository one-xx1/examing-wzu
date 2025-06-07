package com.wzu.exam.service;

import com.wzu.exam.dto.StudentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {
    Page<StudentDTO> getAllStudents(String studentId, String name, Pageable pageable);
    StudentDTO getStudentById(Long id);
    StudentDTO createStudent(StudentDTO studentDTO);
    StudentDTO updateStudent(Long id, StudentDTO studentDTO);
    void deleteStudent(Long id);
    void resetPassword(Long id);
}
