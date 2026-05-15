package com.example.app.controller;

import com.example.app.dto.request.CreateTaskRequest;
import com.example.app.dto.response.TaskResponse;
import com.example.app.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {
    
    private final TaskService taskService;
    
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
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
    
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        TaskResponse task = taskService.getTaskById(id, userId);
        return ResponseEntity.ok(task);
    }
    
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(Authentication authentication, @RequestBody CreateTaskRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        TaskResponse task = taskService.createTask(userId, request);
        return ResponseEntity.ok(task);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody CreateTaskRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        TaskResponse task = taskService.updateTask(id, userId, request);
        return ResponseEntity.ok(task);
    }
    
    @PutMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        TaskResponse task = taskService.completeTask(id, userId);
        return ResponseEntity.ok(task);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        taskService.deleteTask(id, userId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> countTasks(Authentication authentication, @RequestParam String status) {
        Long userId = (Long) authentication.getPrincipal();
        long count = taskService.countTasksByStatus(userId, status);
        return ResponseEntity.ok(count);
    }
}