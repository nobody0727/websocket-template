package com.example.app.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 用户响应DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    // 用户ID
    private Long id;
    // 用户名
    private String username;
    // 邮箱
    private String email;
    // 角色
    private String role;
}