package com.example.app.dto.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 登录响应DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    // JWT令牌
    private String token;
    // 用户信息
    private UserResponse user;
}