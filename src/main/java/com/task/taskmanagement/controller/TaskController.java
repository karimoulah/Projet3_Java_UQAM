package com.task.taskmanagement.controller;

import com.task.taskmanagement.dto.request.CommentRequest;
import com.task.taskmanagement.dto.request.StatusUpdateRequest;
import com.task.taskmanagement.dto.request.ToolRequest;
import com.task.taskmanagement.dto.response.TaskResponse;
import com.task.taskmanagement.dto.response.ToolResponse;
import com.task.taskmanagement.model.Task;
import com.task.taskmanagement.model.User;
import com.task.taskmanagement.model.enums.TaskStatus;
import com.task.taskmanagement.security.RequestUtils;
import com.task.taskmanagement.service.TaskService;
import com.task.taskmanagement.service.TaskMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.EnumMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {

    private final TaskService taskService;
    private final TaskMappingService taskMappingService;

    @Autowired
    public TaskController(TaskService taskService, TaskMappingService taskMappingService) {
        this.taskService = taskService;
        this.taskMappingService = taskMappingService;
    }

    // Lister les taches par status
    @GetMapping("/by-status")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<Map<TaskStatus, List<TaskResponse>>> getTasksByStatus(HttpServletRequest request) {
        User user = RequestUtils.getCurrentUser(request);
        
        Map<TaskStatus, List<Task>> tasksByStatus;
        
        if (user.getRole().equals("ROLE_ADMIN")) {
            tasksByStatus = taskService.getTasksByStatusForAdmin(user.getOrganisationId());
        } else {
            tasksByStatus = taskService.getTasksByStatusForMember(user.getId());
        }
        
        Map<TaskStatus, List<TaskResponse>> response = tasksByStatus.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> taskMappingService.convertToTaskResponses(entry.getValue()),
                (e1, e2) -> e1,
                () -> new EnumMap<>(TaskStatus.class)
            ));
        
        return ResponseEntity.ok(response);
    }
    
    // Basic task operations
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable String id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(taskMappingService.convertToTaskResponse(task));
    }
    
    // Voir les taches d'une organisation
    @GetMapping("/organisations/{organisationId}")
    public ResponseEntity<List<TaskResponse>> getTasksByOrganisation(@PathVariable String organisationId) {
        List<Task> tasks = taskService.getTasksByOrganisationId(organisationId);
        return ResponseEntity.ok(taskMappingService.convertToTaskResponses(tasks));
    }

    // Voir les taches racines d'une organisation
    @GetMapping("/organisations/{organisationId}/root")
    public ResponseEntity<List<TaskResponse>> getRootTasksByOrganisation(@PathVariable String organisationId) {
        List<Task> tasks = taskService.getRootTasksByOrganisationId(organisationId);
        return ResponseEntity.ok(taskMappingService.convertToTaskResponses(tasks));
    }

    
    // Task metrics
    @GetMapping("/{id}/progress")
    public ResponseEntity<Double> getTaskProgress(@PathVariable String id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task.calculateProgress());
    }
    
    // Voir le score d'une tache
    @GetMapping("/{id}/score")
    public ResponseEntity<Integer> getTaskScore(@PathVariable String id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task.calculateTotalScore());
    }
    
    // Liste des outils utilisés pour une tache
    @GetMapping("/{id}/list-tools")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<List<ToolResponse>> getTaskTools(@PathVariable String id, HttpServletRequest request) {
        User user = RequestUtils.getCurrentUser(request);
        return ResponseEntity.ok(taskService.getTaskTools(id, user.getOrganisationId()));
    }
    
    // Ajouter un outil à une tache
    @PostMapping("/{id}/add-tool")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<TaskResponse> addToolToTask(
            @PathVariable String id,
            @Valid @RequestBody ToolRequest toolRequest,
            HttpServletRequest request) {
        User user = RequestUtils.getCurrentUser(request);
        Task updatedTask = taskService.addToolToTask(id, toolRequest.getToolId(), user.getOrganisationId());
        
        
        if (toolRequest.getComment() != null && !toolRequest.getComment().isEmpty()) {
            updatedTask = taskService.addCommentToTask(id, toolRequest.getComment());
        }
        
        return ResponseEntity.ok(taskMappingService.convertToTaskResponse(updatedTask));
    }
    
    // Mettre à jour le statut d'une tache
    @PutMapping("/{id}/update-status")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable String id,
            @Valid @RequestBody StatusUpdateRequest request) {
        Task updatedTask = taskService.updateTaskStatus(id, request.getStatus());
        return ResponseEntity.ok(taskMappingService.convertToTaskResponse(updatedTask));
    }
    
    // Ajouter un commentaire à une tache
    @PutMapping("/{id}/add-comment")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<TaskResponse> addCommentToTask(
            @PathVariable String id,
            @Valid @RequestBody CommentRequest request) {
        Task updatedTask = taskService.addCommentToTask(id, request.getComment());
        return ResponseEntity.ok(taskMappingService.convertToTaskResponse(updatedTask));
    }

    // Ajouter une sous tache à une tache
    @PostMapping("/{id}/add-subtask")
    @PreAuthorize("hasAnyRole('MEMBER', 'ADMIN')")
    public ResponseEntity<TaskResponse> addSubTask(
            @PathVariable String id,
            @Valid @RequestBody Task subTask) {
        Task createdSubTask = taskService.addSubTask(id, subTask);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskMappingService.convertToTaskResponse(createdSubTask));
    }

        
    // Voir les sous taches d'une tache
    @GetMapping("/{id}/subtasks")
    public ResponseEntity<List<TaskResponse>> getSubTasks(@PathVariable String id) {
        List<Task> subTasks = taskService.getSubTasks(id);
        return ResponseEntity.ok(taskMappingService.convertToTaskResponses(subTasks));
    }
    

}