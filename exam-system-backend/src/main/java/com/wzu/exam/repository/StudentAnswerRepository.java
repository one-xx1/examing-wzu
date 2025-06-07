package com.wzu.exam.repository;

import com.wzu.exam.model.Exam;
import com.wzu.exam.model.StudentAnswer;
import com.wzu.exam.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findByUserAndExam(User user, Exam exam);
    boolean existsByUserAndExam(User user, Exam exam);

    @Modifying
    void deleteByUserAndExam(User user, Exam exam);
}
