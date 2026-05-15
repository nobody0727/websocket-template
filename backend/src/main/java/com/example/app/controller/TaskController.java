package com.example.app.controller;

import com.example.app.dto.request.CreateTaskRequest;
import com.example.app.dto.response.TaskResponse;
import com.example.app.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 任务控制器
 * 处理任务管理相关请求
 */
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    // 任务服务
    private final TaskService taskService;
    
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    /**
     * 获取任务列表
     * @param authentication 当前认证用户
     * @param status 任务状态过滤（TODO/DONE）
     * @param page 页码
     * @param size 每页数量
     * @return 分页的任务列表
     */
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getTasks(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = (Long) authentication.getPrincipal();
        Page<TaskResponse> tasks = taskService.getTasks(userId, status, page, size);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * 根据ID获取任务详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        TaskResponse task = taskService.getTaskById(id, userId);
        return ResponseEntity.ok(task);
    }
    
    /**
     * 创建新任务
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(Authentication authentication, @Valid @RequestBody CreateTaskRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        TaskResponse task = taskService.createTask(userId, request);
        return ResponseEntity.ok(task);
    }
    
    /**
     * 更新任务
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody CreateTaskRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        TaskResponse task = taskService.updateTask(id, userId, request);
        return ResponseEntity.ok(task);
    }
    
    /**
     * 完成任务
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        TaskResponse task = taskService.completeTask(id, userId);
        return ResponseEntity.ok(task);
    }
    
    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        taskService.deleteTask(id, userId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * 统计指定状态的任务数量
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTasks(Authentication authentication, @RequestParam String status) {
        Long userId = (Long) authentication.getPrincipal();
        long count = taskService.countTasksByStatus(userId, status);
        return ResponseEntity.ok(count);
    }
}