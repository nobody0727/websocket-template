package com.example.app.service;

import com.example.app.dto.request.BroadcastRequest;
import com.example.app.dto.response.NotificationResponse;
import com.example.app.entity.Notification;
import com.example.app.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * 通知服务
 * 处理通知的获取、标记已读和广播
 */
@Service
public class NotificationService {
    
    // 通知数据仓库
    private final NotificationRepository notificationRepository;
    // WebSocket消息发送模板
    private final SimpMessagingTemplate messagingTemplate;
    // 日期格式化器
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    public NotificationService(NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }
    
    /**
     * 获取通知列表（分页）
     * 包括全局通知（userId为null）和用户个人通知
     */
    public Page<NotificationResponse> getNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notifications = notificationRepository.findByUserIdOrUserIdIsNull(userId, pageable);
        return notifications.map(this::convertToResponse);
    }
    
    /**
     * 根据ID获取通知详情
     */
    public NotificationResponse getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("通知不存在"));
        return convertToResponse(notification);
    }
    
    /**
     * 标记通知为已读
     */
    public NotificationResponse markAsRead(Long id, Long userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("通知不存在"));
        
        // 验证通知归属（全局通知可以被任何人标记）
        if (notification.getUserId() != null && !notification.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此通知");
        }
        
        notification.setIsRead(true);
        Notification updated = notificationRepository.save(notification);
        return convertToResponse(updated);
    }
    
    /**
     * 广播通知
     * 通过WebSocket实时推送给所有用户或指定用户
     */
    public void broadcastNotification(BroadcastRequest request) {
        // 创建通知实体
        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setType(request.getType());
        notification.setUserId(request.getUserId());
        notification.setIsRead(false);
        
        // 保存到数据库
        Notification saved = notificationRepository.save(notification);
        
        // 转换为响应DTO
        NotificationResponse response = convertToResponse(saved);
        
        // 通过WebSocket推送
        if (request.getUserId() != null) {
            // 推送给指定用户
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(request.getUserId()),
                    "/queue/notifications",
                    response
            );
        } else {
            // 广播给所有用户
            messagingTemplate.convertAndSend("/topic/notifications", response);
        }
    }
    
    /**
     * 统计未读通知数量
     */
    public long countUnreadNotifications(Long userId) {
        // 统计用户个人未读通知
        long userUnread = notificationRepository.countByUserIdAndIsRead(userId, false);
        // 统计全局未读通知
        long globalUnread = notificationRepository.countByUserIdIsNullAndIsRead(false);
        return userUnread + globalUnread;
    }
    
    /**
     * 将Notification实体转换为NotificationResponse DTO
     */
    private NotificationResponse convertToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setTitle(notification.getTitle());
        response.setContent(notification.getContent());
        response.setType(notification.getType());
        response.setIsRead(notification.getIsRead());
        response.setCreatedAt(notification.getCreatedAt().format(formatter));
        response.setUserId(notification.getUserId());
        return response;
    }
}