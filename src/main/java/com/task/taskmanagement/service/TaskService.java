package com.task.taskmanagement.service;

import com.task.taskmanagement.exception.ResourceNotFoundException;
import com.task.taskmanagement.model.Member;
import com.task.taskmanagement.model.Task;
import com.task.taskmanagement.model.Tool;
import com.task.taskmanagement.model.enums.TaskStatus;
import com.task.taskmanagement.repository.MemberRepository;
import com.task.taskmanagement.repository.TaskRepository;
import com.task.taskmanagement.repository.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import com.task.taskmanagement.dto.response.ToolResponse;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskSequenceService taskSequenceService;
    private final MemberRepository memberRepository;
    private final ToolRepository toolRepository;
    private final ToolService toolService;
    
    @Autowired
    public TaskService(TaskRepository taskRepository,
                      TaskSequenceService taskSequenceService,
                      MemberRepository memberRepository,
                      ToolRepository toolRepository,
                      ToolService toolService) {
        this.taskRepository = taskRepository;
        this.taskSequenceService = taskSequenceService;
        this.memberRepository = memberRepository;
        this.toolRepository = toolRepository;
        this.toolService = toolService;
    }
    
    public Task getTaskById(String id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tâche non trouvée avec l'ID: " + id));
    }
    
    public List<Task> getTasksByOrganisationId(String organisationId) {
        return taskRepository.findByOrganisationId(organisationId);
    }
    
    public List<Task> getRootTasksByOrganisationId(String organisationId) {
        return taskRepository.findRootTasksByOrganisationId(organisationId);
    }
    
    public List<Task> getSubTasks(String parentTaskId) {
        return taskRepository.findByParentTaskId(parentTaskId);
    }
    
    @Transactional
    public Task updateTaskStatus(String taskId, TaskStatus newStatus) {
        Task task = getTaskById(taskId);
        TaskStatus oldStatus = task.getStatus();
        
        task.updateStatus(newStatus);
        Task updatedTask = taskRepository.save(task);
        
        // Si la tâche est terminée et était une tâche principale, créer la tâche suivante
        if (newStatus == TaskStatus.DONE && oldStatus != TaskStatus.DONE && task.getParentTaskId() == null) {
            Task nextTask = taskSequenceService.createNextTaskInSequence(task);
            if (nextTask != null) {
                nextTask.setAssignedMemberId(task.getAssignedMemberId());
                taskRepository.save(nextTask);
                
                // Mettre à jour le score du membre
                if (task.getAssignedMemberId() != null) {
                    Member member = memberRepository.findById(task.getAssignedMemberId())
                        .orElseThrow(() -> new ResourceNotFoundException("Membre non trouvé"));
                    member.setScore(member.getScore() + task.calculateTotalScore());
                    memberRepository.save(member);
                }
            }
        }
        
        return updatedTask;
    }
    
    @Transactional
    public Task addSubTask(String parentTaskId, Task subTask) {
        Task parentTask = getTaskById(parentTaskId);

        if(!parentTask.getType().equals(subTask.getType())) {
            throw new IllegalStateException("Vous ne pouvez pas ajouter une sous tache de type " + subTask.getType() + " à une tache de type " + parentTask.getType());
        }
        
        subTask.setParentTaskId(parentTaskId);
        subTask.setOrganisationId(parentTask.getOrganisationId());
        subTask.setCategory(parentTask.getCategory());
        
        Task savedSubTask = taskRepository.save(subTask);
        
        parentTask.addSubTaskId(savedSubTask.getId());
        taskRepository.save(parentTask);
        
        return savedSubTask;
    }
    
    @Transactional
    public Task addToolToTask(String taskId, String toolId, String organisationId) {
        Task task = getTaskById(taskId);
        Tool tool = toolRepository.findById(toolId)
            .orElseThrow(() -> new ResourceNotFoundException("Outil non trouvé avec l'ID: " + toolId));
        if (!tool.isAvailable()) {
            throw new IllegalStateException("Cet outil n'est pas disponible");
        }
        if (!task.getOrganisationId().equals(organisationId)) {
            throw new IllegalStateException("Vous n'avez pas le droit d'utiliser cet outil");
        }
        
        task.addToolId(toolId);
        tool.getUsedInTaskIds().add(taskId);
        tool.setAvailable(false);
        
        toolRepository.save(tool);
        return taskRepository.save(task);
    }
    
    @Transactional
    public Task addCommentToTask(String taskId, String comment) {
        Task task = getTaskById(taskId);
        
        if (task.getComment() == null || task.getComment().isEmpty()) {
            task.setComment(comment);
        } else {
            task.setComment(task.getComment() + "\n" + comment);
        }
        
        return taskRepository.save(task);
    }

    public Map<TaskStatus, List<Task>> getTasksByStatusForAdmin(String organisationId) {
        List<Task> tasks = taskRepository.findByOrganisationId(organisationId);
        return groupTasksByStatus(tasks);
    }

    public Map<TaskStatus, List<Task>> getTasksByStatusForMember(String memberId) {
        List<Task> tasks = taskRepository.findByAssignedMemberId(memberId);
        return groupTasksByStatus(tasks);
    }

    private Map<TaskStatus, List<Task>> groupTasksByStatus(List<Task> tasks) {
        return tasks.stream()
                .collect(Collectors.groupingBy(
                    Task::getStatus,
                    () -> new EnumMap<>(TaskStatus.class),
                    Collectors.toList()
                ));
    }

    public List<ToolResponse> getTaskTools(String taskId, String organisationId) {
        Task task = getTaskById(taskId);
        if (!task.getOrganisationId().equals(organisationId)) {
            throw new IllegalStateException("Vous ne pouvez pas accéder à ces outils");
        }
        List<Tool> tools = toolRepository.findAllById(task.getToolIds());
        return tools.stream()
            .map(toolService::convertToToolResponse)
            .collect(Collectors.toList());
    }
}