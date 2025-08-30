package com.task.taskmanagement.dto.response;

import com.task.taskmanagement.model.Task;
import com.task.taskmanagement.model.enums.TaskStatus;
import com.task.taskmanagement.model.enums.TaskType;
import com.task.taskmanagement.model.enums.TaskCategory;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class TaskResponse {
    private String id;
    private String description;
    private TaskType type;
    private TaskCategory category;
    private TaskStatus status;
    private String comment;
    private double progress;
    private int score;
    private MemberResponse assignedMember;
    private OrganisationResponse organisation;
    private List<ToolResponse> tools;
    private List<TaskResponse> subTasks;
    private String parentTaskId;

    public static TaskResponse fromTask(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setDescription(task.getDescription());
        response.setType(task.getType());
        response.setCategory(task.getCategory());
        response.setStatus(task.getStatus());
        response.setComment(task.getComment());
        response.setProgress(task.calculateProgress());
        response.setScore(task.calculateTotalScore());
        response.setParentTaskId(task.getParentTaskId());
        return response;
    }
}