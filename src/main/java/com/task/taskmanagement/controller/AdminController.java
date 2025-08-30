package com.task.taskmanagement.controller;

import com.task.taskmanagement.dto.response.OrganisationResponse;
import com.task.taskmanagement.dto.response.UserResponse;
import com.task.taskmanagement.model.User;
import com.task.taskmanagement.service.OrganisationService;
import com.task.taskmanagement.service.UserService;
import com.task.taskmanagement.security.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final OrganisationService organisationService;
    private final UserService userService;

    public AdminController(
            OrganisationService organisationService,
            UserService userService) {
        this.organisationService = organisationService;
        this.userService = userService;
    }

    // Voir l'organisation actuelle
    @GetMapping("/organisations/current")
    public ResponseEntity<OrganisationResponse> getCurrentOrganisation(HttpServletRequest request) {
        User admin = RequestUtils.getCurrentUser(request);
        OrganisationResponse response = organisationService.getOrganisationInfo(admin.getOrganisationId());
        return ResponseEntity.ok(response);
    }

    // Voir les membres de l'organisation
    @GetMapping("/organisations/members")
    public ResponseEntity<List<UserResponse>> getMembersByOrganisationId(HttpServletRequest request) {
        User admin = RequestUtils.getCurrentUser(request);
        return ResponseEntity.ok(userService.getMembersByOrganisationId(admin.getOrganisationId()));
    }

    // Voir un membre de l'organisation
    @GetMapping("/members/{id}")
    public ResponseEntity<UserResponse> getMemberById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getMemberById(id));
    }
}