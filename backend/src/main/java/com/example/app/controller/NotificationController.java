package com.example.app.controller;

import com.example.app.dto.request.BroadcastRequest;
import com.example.app.dto.response.NotificationResponse;
import com.example.app.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 通知控制器
 * 处理通知相关请求
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    // 通知服务
    private final NotificationService notificationService;
    
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    /**
     * 获取通知列表（分页）
     */
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) authentication.getPrincipal();
        Page<NotificationResponse> notifications = notificationService.getNotifications(userId, page, size);
        return ResponseEntity.ok(notifications);
    }
    
    /**
     * 根据ID获取通知详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        NotificationResponse notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }
    
    /**
     * 标记通知为已读
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        NotificationResponse notification = notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(notification);
    }
    
    /**
     * 广播通知（管理员）
     */
    @PostMapping("/broadcast")
    public ResponseEntity<Void> broadcastNotification(@Valid @RequestBody BroadcastRequest request) {
        notificationService.broadcastNotification(request);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 获取未读通知数量
     */
    @GetMapping("/count/unread")
    public ResponseEntity<Long> countUnread(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(count);
    }
}