package com.example.app.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String priority;
    private String status;
    private String deadline;
    private String createdAt;
    private String updatedAt;
    private Long userId;
}