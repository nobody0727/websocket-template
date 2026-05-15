package com.example.app.dto.request;

import lombok.Data;

@Data
public class BroadcastRequest {
    private String title;
    private String content;
    private String type = "INFO";
    private Long userId;
}