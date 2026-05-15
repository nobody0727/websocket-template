package com.example.app.repository;

import com.example.app.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 通知数据仓库接口
 * 继承JpaRepository提供基础的CRUD操作
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 查找全局通知或指定用户的通知（分页）
    Page<Notification> findByUserIdOrUserIdIsNull(Long userId, Pageable pageable);
    // 根据用户ID查找通知（分页）
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    // 统计指定用户指定状态的通知数量
    long countByUserIdAndIsRead(Long userId, Boolean isRead);
    // 统计全局指定状态的通知数量
    long countByUserIdIsNullAndIsRead(Boolean isRead);
}