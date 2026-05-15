package com.example.app.repository;

import com.example.app.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByUserId(Long userId, Pageable pageable);
    Page<Task> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
    Page<Task> findByUserIdAndPriority(Long userId, String priority, Pageable pageable);
    long countByUserIdAndStatus(Long userId, String status);
}