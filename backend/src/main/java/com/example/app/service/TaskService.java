package com.example.app.service;

import com.example.app.dto.request.CreateTaskRequest;
import com.example.app.dto.response.TaskResponse;
import com.example.app.entity.Task;
import com.example.app.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 任务服务
 * 处理任务的CRUD操作
 */
@Service
public class TaskService {
    
    // 任务数据仓库
    private final TaskRepository taskRepository;
    // 日期格式化器
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    /**
     * 获取用户任务列表（分页）
     */
    public Page<TaskResponse> getTasks(Long userId, String status, int page, int size) {
        // 创建分页对象，按创建时间倒序
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Task> tasks;
        // 根据状态过滤
        if (status != null && !status.isEmpty()) {
            tasks = taskRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            tasks = taskRepository.findByUserId(userId, pageable);
        }
        
        return tasks.map(this::convertToResponse);
    }
    
    /**
     * 根据ID获取任务
     */
    public TaskResponse getTaskById(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        // 验证任务归属
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问此任务");
        }
        
        return convertToResponse(task);
    }
    
    /**
     * 创建新任务
     */
    public TaskResponse createTask(Long userId, CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus("TODO"); // 新任务默认为TODO状态
        // 解析截止日期
        if (request.getDeadline() != null && !request.getDeadline().isEmpty()) {
            task.setDeadline(LocalDate.parse(request.getDeadline(), formatter));
        }
        task.setUserId(userId);
        
        Task savedTask = taskRepository.save(task);
        return convertToResponse(savedTask);
    }
    
    /**
     * 更新任务
     */
    public TaskResponse updateTask(Long id, Long userId, CreateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        // 验证任务归属
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此任务");
        }
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        if (request.getDeadline() != null && !request.getDeadline().isEmpty()) {
            task.setDeadline(LocalDate.parse(request.getDeadline(), formatter));
        }
        
        Task updatedTask = taskRepository.save(task);
        return convertToResponse(updatedTask);
    }
    
    /**
     * 完成任务
     */
    public TaskResponse completeTask(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此任务");
        }
        
        // 将任务状态设置为DONE
        task.setStatus("DONE");
        Task updatedTask = taskRepository.save(task);
        return convertToResponse(updatedTask);
    }
    
    /**
     * 删除任务
     */
    public void deleteTask(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("任务不存在"));
        
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此任务");
        }
        
        taskRepository.delete(task);
    }
    
    /**
     * 统计指定状态的任务数量
     */
    public long countTasksByStatus(Long userId, String status) {
        return taskRepository.countByUserIdAndStatus(userId, status);
    }
    
    /**
     * 将Task实体转换为TaskResponse DTO
     */
    private TaskResponse convertToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setPriority(task.getPriority());
        response.setStatus(task.getStatus());
        response.setDeadline(task.getDeadline() != null ? task.getDeadline().format(formatter) : null);
        response.setCreatedAt(task.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.setUpdatedAt(task.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.setUserId(task.getUserId());
        return response;
    }
}