package com.wzu.exam.controller;

import com.wzu.exam.dto.UserDTO;
import com.wzu.exam.model.User;
import com.wzu.exam.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin/students")
public class AdminStudentController {
    private static final Logger logger = LoggerFactory.getLogger(AdminStudentController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<User>> getStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String name) {
        logger.info("Getting students page {} size {}, username: {}, name: {}", page, size, username, name);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> students;
            
            if (username != null && !username.isEmpty() && name != null && !name.isEmpty()) {
                students = userService.getUsersByRoleAndUsernameAndName("STUDENT", username, name, pageable);
            } else if (username != null && !username.isEmpty()) {
                students = userService.getUsersByRoleAndUsername("STUDENT", username, pageable);
            } else if (name != null && !name.isEmpty()) {
                students = userService.getUsersByRoleAndName("STUDENT", name, pageable);
            } else {
                students = userService.getUsersByRole("STUDENT", pageable);
            }
            
            logger.info("Retrieved {} students", students.getTotalElements());
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            logger.error("Error getting students", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getStudent(@PathVariable Long id) {
        logger.info("Getting student with id: {}", id);
        try {
            User student = userService.getUserById(id)
                    .filter(user -> "STUDENT".equals(user.getRole()))
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
            
            logger.info("Student retrieved successfully: {}", student.getUsername());
            return ResponseEntity.ok(student);
        } catch (Exception e) {
            logger.error("Error getting student", e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<User> createStudent(@RequestBody UserDTO userDTO) {
        logger.info("Creating new student: {}", userDTO.getUsername());
        try {
            userDTO.setRole("STUDENT");
            userDTO.setActive(true);
            
            User savedStudent = userService.createUser(userDTO);
            logger.info("Student created successfully: {}", savedStudent.getUsername());
            return ResponseEntity.ok(savedStudent);
        } catch (Exception e) {
            logger.error("Error creating student", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateStudent(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        logger.info("Updating student with id {}: {}", id, userDTO.getUsername());
        try {
            // 先检查是否是学生
            User existingStudent = userService.getUserById(id)
                    .filter(user -> "STUDENT".equals(user.getRole()))
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
            
            // 确保不会更改角色
            userDTO.setRole("STUDENT");
            
            User updatedStudent = userService.updateUser(id, userDTO);
            logger.info("Student updated successfully: {}", updatedStudent.getUsername());
            return ResponseEntity.ok(updatedStudent);
        } catch (Exception e) {
            logger.error("Error updating student", e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        logger.info("Deleting student with id: {}", id);
        try {
            Optional<User> studentOpt = userService.getUserById(id);
            if (studentOpt.isPresent() && "STUDENT".equals(studentOpt.get().getRole())) {
                userService.deleteUser(id);
                logger.info("Student deleted successfully");
                return ResponseEntity.ok().build();
            } else {
                throw new RuntimeException("Student not found with id: " + id);
            }
        } catch (Exception e) {
            logger.error("Error deleting student", e);
            throw e;
        }
    }
    
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable Long id) {
        logger.info("Resetting password for student with id: {}", id);
        try {
            User student = userService.getUserById(id)
                    .filter(user -> "STUDENT".equals(user.getRole()))
                    .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
            
            // 重置为默认密码 "123456"
            userService.resetPassword(id, "123456");
            
            logger.info("Password reset successfully for student: {}", student.getUsername());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error resetting password", e);
            throw e;
        }
    }
}
