package com.example.app.dto.request;

import lombok.Data;

/**
 * 广播通知请求DTO
 */
@Data
public class BroadcastRequest {
    // 通知标题
    private String title;
    // 通知内容
    private String content;
    // 通知类型（INFO/SUCCESS/WARNING/ERROR）
    private String type = "INFO";
    // 指定用户ID，为空则广播给所有用户
    private Long userId;
}