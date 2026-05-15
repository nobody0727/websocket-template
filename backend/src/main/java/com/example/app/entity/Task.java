package com.example.app.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 任务实体类
 * 对应数据库中的tasks表
 */
@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    
    // 任务ID，主键，自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 任务标题，不能为空，最大200字符
    @Column(nullable = false, length = 200)
    private String title;
    
    // 任务描述，可为空
    @Column(columnDefinition = "TEXT")
    private String description;
    
    // 优先级，默认MEDIUM（HIGH/MEDIUM/LOW）
    @Column(nullable = false, length = 20)
    private String priority = "MEDIUM";
    
    // 状态，默认TODO（TODO/DONE）
    @Column(nullable = false, length = 20)
    private String status = "TODO";
    
    // 截止日期
    @Column(name = "deadline")
    private LocalDate deadline;
    
    // 用户ID，外键关联users表
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    // 创建时间
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // 更新时间
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // 实体创建前自动设置创建时间和更新时间
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    // 实体更新前自动更新时间
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}