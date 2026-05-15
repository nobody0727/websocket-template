package com.example.app.controller;

import com.example.app.dto.response.UserResponse;
import com.example.app.entity.User;
import com.example.app.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户控制器
 * 处理用户管理相关请求（仅管理员可访问）
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    // 用户服务
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 获取所有用户列表
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * 根据ID获取用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    /**
     * 创建新用户
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody User user) {
        UserResponse created = userService.createUser(user);
        return ResponseEntity.ok(created);
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody User user) {
        UserResponse updated = userService.updateUser(id, user);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}