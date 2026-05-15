package com.example.app.dto.request;

import lombok.Data;

/**
 * 创建任务请求DTO
 */
@Data
public class CreateTaskRequest {
    // 任务标题
    private String title;
    // 任务描述
    private String description;
    // 优先级（HIGH/MEDIUM/LOW）
    private String priority = "MEDIUM";
    // 截止日期（格式：yyyy-MM-dd）
    private String deadline;
}