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

@Service
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    
    public NotificationService(NotificationRepository notificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }
    
    public Page<NotificationResponse> getNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> notifications = notificationRepository.findByUserIdOrUserIdIsNull(userId, pageable);
        return notifications.map(this::convertToResponse);
    }
    
    public NotificationResponse getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        return convertToResponse(notification);
    }
    
    public NotificationResponse markAsRead(Long id, Long userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (notification.getUserId() != null && !notification.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        notification.setIsRead(true);
        Notification updated = notificationRepository.save(notification);
        return convertToResponse(updated);
    }
    
    public void broadcastNotification(BroadcastRequest request) {
        Notification notification = new Notification();
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setType(request.getType());
        notification.setUserId(request.getUserId());
        notification.setIsRead(false);
        
        Notification saved = notificationRepository.save(notification);
        
        NotificationResponse response = convertToResponse(saved);
        
        if (request.getUserId() != null) {
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(request.getUserId()),
                    "/queue/notifications",
                    response
            );
        } else {
            messagingTemplate.convertAndSend("/topic/notifications", response);
        }
    }
    
    public long countUnreadNotifications(Long userId) {
        long userUnread = notificationRepository.countByUserIdAndIsRead(userId, false);
        long globalUnread = notificationRepository.countByUserIdIsNullAndIsRead(false);
        return userUnread + globalUnread;
    }
    
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