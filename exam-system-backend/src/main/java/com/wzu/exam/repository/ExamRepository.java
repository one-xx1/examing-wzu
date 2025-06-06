package com.wzu.exam.repository;

import com.wzu.exam.model.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 考试仓库接口
 */
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    /**
     * 查找所有激活的考试
     * @return 激活的考试列表
     */
    List<Exam> findByIsActiveTrue();

    /**
     * 分页查找激活的考试
     * @param pageable 分页参数
     * @return 考试分页结果
     */
    Page<Exam> findByIsActiveTrue(Pageable pageable);
}