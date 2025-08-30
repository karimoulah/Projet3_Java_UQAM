package com.task.taskmanagement.service;

import com.task.taskmanagement.dto.response.TaskResponse;
import com.task.taskmanagement.dto.response.MemberResponse;
import com.task.taskmanagement.dto.response.ToolResponse;
import com.task.taskmanagement.model.Task;
import com.task.taskmanagement.model.Member;
import com.task.taskmanagement.model.Tool;
import com.task.taskmanagement.repository.MemberRepository;
import com.task.taskmanagement.repository.ToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskMappingService {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private OrganisationService organisationService;

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private ToolRepository toolRepository;

    public TaskResponse convertToTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setDescription(task.getDescription());
        response.setType(task.getType());
        response.setCategory(task.getCategory());
        response.setStatus(task.getStatus());
        response.setComment(task.getComment());
        response.setProgress(task.calculateProgress());
        response.setScore(task.calculateTotalScore());
        
        if (task.getAssignedMemberId() != null) {
            Member member = memberRepository.findById(task.getAssignedMemberId())
                .orElse(null);
            if (member != null) {
                response.setAssignedMember(MemberResponse.fromMember(member));
            }
        }
        
        if (task.getOrganisationId() != null) {
            response.setOrganisation(organisationService.getOrganisationInfo(task.getOrganisationId()));
        }
        
        if (task.getToolIds() != null && !task.getToolIds().isEmpty()) {
            List<Tool> tools = toolRepository.findAllById(task.getToolIds());
            response.setTools(tools.stream()
                .map(ToolResponse::fromTool)
                .collect(Collectors.toList()));
        }
        
        if (task.getSubTaskIds() != null && !task.getSubTaskIds().isEmpty()) {
            response.setSubTasks(task.getSubTaskIds().stream()
                .map(subTaskId -> convertToTaskResponse(taskService.getTaskById(subTaskId)))
                .collect(Collectors.toList()));
        }
        
        response.setParentTaskId(task.getParentTaskId());
        return response;
    }

    public List<TaskResponse> convertToTaskResponses(List<Task> tasks) {
        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }
}