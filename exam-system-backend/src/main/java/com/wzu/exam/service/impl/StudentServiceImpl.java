package com.wzu.exam.service.impl;

import com.wzu.exam.dto.StudentDTO;
import com.wzu.exam.model.User;
import com.wzu.exam.repository.UserRepository;
import com.wzu.exam.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public Page<StudentDTO> getAllStudents(String studentId, String name, Pageable pageable) {
        Page<User> students;
        
        if (studentId != null && !studentId.isEmpty() && name != null && !name.isEmpty()) {
            students = userRepository.findByRoleAndUsernameContainingAndNameContaining(
                    "STUDENT", studentId, name, pageable);
        } else if (studentId != null && !studentId.isEmpty()) {
            students = userRepository.findByRoleAndUsernameContaining(
                    "STUDENT", studentId, pageable);
        } else if (name != null && !name.isEmpty()) {
            students = userRepository.findByRoleAndNameContaining(
                    "STUDENT", name, pageable);
        } else {
            students = userRepository.findByRole("STUDENT", pageable);
        }
        
        return students.map(this::convertToDTO);
    }

    @Override
    public StudentDTO getStudentById(Long id) {
        User student = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        if (!"STUDENT".equals(student.getRole())) {
            throw new RuntimeException("User is not a student");
        }
        
        return convertToDTO(student);
    }

    @Override
    @Transactional
    public StudentDTO createStudent(StudentDTO studentDTO) {
        if (userRepository.findByUsername(studentDTO.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        
        User student = new User();
        student.setUsername(studentDTO.getUsername());
        student.setPassword(passwordEncoder.encode(studentDTO.getPassword()));
        student.setName(studentDTO.getName());
        student.setGender(studentDTO.getGender());
        student.setClassName(studentDTO.getClassName());
        student.setEmail(studentDTO.getEmail());
        student.setPhone(studentDTO.getPhone());
        student.setActive(true);
        student.setRole("STUDENT");
        
        User savedStudent = userRepository.save(student);
        return convertToDTO(savedStudent);
    }

    @Override
    @Transactional
    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        User student = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        if (!"STUDENT".equals(student.getRole())) {
            throw new RuntimeException("User is not a student");
        }
        
        // Check if username is being changed and if it already exists
        if (!student.getUsername().equals(studentDTO.getUsername())) {
            Optional<User> existingUser = userRepository.findByUsername(studentDTO.getUsername());
            if (existingUser.isPresent()) {
                throw new RuntimeException("Username already exists");
            }
            student.setUsername(studentDTO.getUsername());
        }
        
        student.setName(studentDTO.getName());
        student.setGender(studentDTO.getGender());
        student.setClassName(studentDTO.getClassName());
        student.setEmail(studentDTO.getEmail());
        student.setPhone(studentDTO.getPhone());
        
        User updatedStudent = userRepository.save(student);
        return convertToDTO(updatedStudent);
    }

    @Override
    @Transactional
    public void deleteStudent(Long id) {
        User student = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        if (!"STUDENT".equals(student.getRole())) {
            throw new RuntimeException("User is not a student");
        }
        
        userRepository.delete(student);
    }

    @Override
    @Transactional
    public void resetPassword(Long id) {
        User student = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        
        if (!"STUDENT".equals(student.getRole())) {
            throw new RuntimeException("User is not a student");
        }
        
        student.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        userRepository.save(student);
    }
    
    private StudentDTO convertToDTO(User user) {
        return StudentDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .gender(user.getGender())
                .className(user.getClassName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .active(user.isActive())
                .build();
    }
}
