package com.example.app.repository;

import com.example.app.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUserIdOrUserIdIsNull(Long userId, Pageable pageable);
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    long countByUserIdAndIsRead(Long userId, Boolean isRead);
    long countByUserIdIsNullAndIsRead(Boolean isRead);
}