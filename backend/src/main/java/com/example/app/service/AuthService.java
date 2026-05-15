package com.example.app.service;

import com.example.app.config.JwtTokenProvider;
import com.example.app.dto.request.LoginRequest;
import com.example.app.dto.response.LoginResponse;
import com.example.app.dto.response.UserResponse;
import com.example.app.entity.User;
import com.example.app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 * 处理用户登录验证和令牌生成
 */
@Service
public class AuthService {
    
    // 用户数据仓库
    private final UserRepository userRepository;
    // 密码编码器
    private final PasswordEncoder passwordEncoder;
    // JWT令牌提供者
    private final JwtTokenProvider tokenProvider;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }
    
    /**
     * 用户登录
     * @param request 登录请求（用户名和密码）
     * @return 登录响应（JWT令牌和用户信息）
     */
    public LoginResponse login(LoginRequest request) {
        // 根据用户名查找用户
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 生成JWT令牌
        String token = tokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 构建用户响应
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setRole(user.getRole());
        
        return new LoginResponse(token, userResponse);
    }
}