package com.task.taskmanagement.dto.response;

import com.task.taskmanagement.model.Member;
import lombok.Data;

@Data
public class MemberResponse {
    private String id;
    private String username;
    private String name;
    private String email;
    private String role;
    private String userType;
    private int score;

    public static MemberResponse fromMember(Member member) {
        MemberResponse response = new MemberResponse();
        response.setId(member.getId());
        response.setUsername(member.getUsername());
        response.setName(member.getName());
        response.setEmail(member.getEmail());
        response.setRole(member.getRole());
        response.setUserType(member.getClass().getSimpleName());
        response.setScore(member.getScore());
        return response;
    }
} 