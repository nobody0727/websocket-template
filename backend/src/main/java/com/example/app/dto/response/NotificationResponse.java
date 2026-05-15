package com.example.app.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 通知响应DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    // 通知ID
    private Long id;
    // 通知标题
    private String title;
    // 通知内容
    private String content;
    // 通知类型
    private String type;
    // 是否已读
    private Boolean isRead;
    // 创建时间
    private String createdAt;
    // 用户ID（为空表示全局通知）
    private Long userId;
}