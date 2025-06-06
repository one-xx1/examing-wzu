package com.wzu.exam.repository;

import com.wzu.exam.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByExamId(Long examId);
    
    // 分页查询
    Page<Question> findByExamId(Long examId, Pageable pageable);
    
    // 统计考试题目数量
    @Query("SELECT COUNT(q) FROM Question q WHERE q.exam.id = ?1")
    int countByExamId(Long examId);
    
    // 按类型查询，如果需要按类型查询，需要在Question实体中添加type字段
    // 这里暂时不实现，因为目前只有单选题类型
}