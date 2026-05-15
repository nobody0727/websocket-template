package com.example.app.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 任务响应DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
    // 任务ID
    private Long id;
    // 任务标题
    private String title;
    // 任务描述
    private String description;
    // 优先级
    private String priority;
    // 状态
    private String status;
    // 截止日期
    private String deadline;
    // 创建时间
    private String createdAt;
    // 更新时间
    private String updatedAt;
    // 用户ID
    private Long userId;
}