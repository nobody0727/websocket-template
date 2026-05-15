package com.example.app.controller;

import com.example.app.dto.request.BroadcastRequest;
import com.example.app.dto.response.NotificationResponse;
import com.example.app.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) authentication.getPrincipal();
        Page<NotificationResponse> notifications = notificationService.getNotifications(userId, page, size);
        return ResponseEntity.ok(notifications);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        NotificationResponse notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        NotificationResponse notification = notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(notification);
    }
    
    @PostMapping("/broadcast")
    public ResponseEntity<Void> broadcastNotification(@RequestBody BroadcastRequest request) {
        notificationService.broadcastNotification(request);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/count/unread")
    public ResponseEntity<Long> countUnread(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        long count = notificationService.countUnreadNotifications(userId);
        return ResponseEntity.ok(count);
    }
}