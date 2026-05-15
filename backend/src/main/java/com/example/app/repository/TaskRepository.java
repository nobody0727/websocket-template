package com.example.app.repository;

import com.example.app.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 任务数据仓库接口
 * 继承JpaRepository提供基础的CRUD操作
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // 根据用户ID查找任务（分页）
    Page<Task> findByUserId(Long userId, Pageable pageable);
    // 根据用户ID和状态查找任务（分页）
    Page<Task> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
    // 根据用户ID和优先级查找任务（分页）
    Page<Task> findByUserIdAndPriority(Long userId, String priority, Pageable pageable);
    // 统计指定用户指定状态的任务数量
    long countByUserIdAndStatus(Long userId, String status);
}