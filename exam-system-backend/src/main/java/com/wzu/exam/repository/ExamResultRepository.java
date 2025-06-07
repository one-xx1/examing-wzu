package com.wzu.exam.repository;

import com.wzu.exam.model.Exam;
import com.wzu.exam.model.ExamResult;
import com.wzu.exam.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    Page<ExamResult> findAll(Pageable pageable);

    List<ExamResult> findByUserOrderByCreatedAtDesc(User user);

    // 分页查询用户的考试结果
    Page<ExamResult> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    List<ExamResult> findByExamOrderByScoreDesc(Exam exam);

    boolean existsByUserAndExam(User user, Exam exam);

    // 管理员查询相关方法
    Page<ExamResult> findByExamId(Long examId, Pageable pageable);

    @Query("SELECT er FROM ExamResult er WHERE er.user.username LIKE %:name% OR er.user.name LIKE %:name%")
    Page<ExamResult> findByUserNameContaining(@Param("name") String name, Pageable pageable);

    @Query("SELECT er FROM ExamResult er WHERE er.exam.id = :examId AND (er.user.username LIKE %:name% OR er.user.name LIKE %:name%)")
    Page<ExamResult> findByExamIdAndUserNameContaining(@Param("examId") Long examId, @Param("name") String name, Pageable pageable);
}
