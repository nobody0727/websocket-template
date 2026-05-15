package com.example.app.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库中的users表
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    // 用户ID，主键，自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 用户名，唯一，不能为空，最大50字符
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    // 邮箱，唯一，不能为空，最大100字符
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    // 密码，不能为空，最大255字符（存储BCrypt加密后的密码）
    @Column(nullable = false, length = 255)
    private String password;
    
    // 角色，默认USER（可设置为ADMIN）
    @Column(nullable = false, length = 20)
    private String role = "USER";
    
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