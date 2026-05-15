package com.example.app.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 通知实体类
 * 对应数据库中的notifications表
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    
    // 通知ID，主键，自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 通知标题，不能为空，最大200字符
    @Column(nullable = false, length = 200)
    private String title;
    
    // 通知内容，不能为空
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    // 通知类型，默认INFO（INFO/SUCCESS/WARNING/ERROR）
    @Column(nullable = false, length = 20)
    private String type = "INFO";
    
    // 是否已读，默认false
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    // 用户ID，可为空（为空表示全局通知）
    @Column(name = "user_id")
    private Long userId;
    
    // 创建时间
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // 实体创建前自动设置创建时间
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}