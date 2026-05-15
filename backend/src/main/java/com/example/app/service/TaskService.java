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

@Service
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    public Page<TaskResponse> getTasks(Long userId, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Task> tasks;
        if (status != null && !status.isEmpty()) {
            tasks = taskRepository.findByUserIdAndStatus(userId, status, pageable);
        } else {
            tasks = taskRepository.findByUserId(userId, pageable);
        }
        
        return tasks.map(this::convertToResponse);
    }
    
    public TaskResponse getTaskById(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        return convertToResponse(task);
    }
    
    public TaskResponse createTask(Long userId, CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setStatus("TODO");
        if (request.getDeadline() != null && !request.getDeadline().isEmpty()) {
            task.setDeadline(LocalDate.parse(request.getDeadline(), formatter));
        }
        task.setUserId(userId);
        
        Task savedTask = taskRepository.save(task);
        return convertToResponse(savedTask);
    }
    
    public TaskResponse updateTask(Long id, Long userId, CreateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
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
    
    public TaskResponse completeTask(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        task.setStatus("DONE");
        Task updatedTask = taskRepository.save(task);
        return convertToResponse(updatedTask);
    }
    
    public void deleteTask(Long id, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        taskRepository.delete(task);
    }
    
    public long countTasksByStatus(Long userId, String status) {
        return taskRepository.countByUserIdAndStatus(userId, status);
    }
    
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