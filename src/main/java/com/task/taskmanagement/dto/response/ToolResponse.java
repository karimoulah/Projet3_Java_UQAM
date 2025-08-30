package com.task.taskmanagement.dto.response;

import com.task.taskmanagement.model.Tool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolResponse {
    private String id;
    private String name;
    private String type;
    private boolean available;
    private OrganisationResponse organisation;

    public static ToolResponse fromTool(Tool tool) {
        ToolResponse response = new ToolResponse();
        response.setId(tool.getId());
        response.setName(tool.getName());
        response.setType(tool.getType());
        response.setAvailable(tool.isAvailable());
        return response;
    }
}
