package com.task.taskmanagement.controller;

import com.task.taskmanagement.dto.response.TaskResponse;
import com.task.taskmanagement.dto.response.UserResponse;
import com.task.taskmanagement.model.Task;
import com.task.taskmanagement.service.MemberService;
import com.task.taskmanagement.service.TaskMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/members")
@PreAuthorize("hasRole('ROLE_MEMBER')")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final TaskMappingService taskMappingService;

    // Voir le profil du membre
    @GetMapping("/me/profile")
    public ResponseEntity<UserResponse> getUserProfile(Authentication authentication) {
        return ResponseEntity.ok(memberService.getCurrentMemberProfile(authentication));
    }

    // Voir les taches du membre
    @GetMapping("/me/tasks")
    public ResponseEntity<List<TaskResponse>> getUserTasks(Authentication authentication) {
        List<Task> tasks = memberService.getMemberTasks(authentication);
        return ResponseEntity.ok(taskMappingService.convertToTaskResponses(tasks));
    }
}