package com.task.taskmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ToolRequest {
    @NotBlank(message = "L'ID de l'outil est requis")
    private String toolId;
    
    private String comment;
}
