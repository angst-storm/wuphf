package com.kiprin.ums.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kiprin.common.web.ApiResponse;
import com.kiprin.ums.domain.Role;
import com.kiprin.ums.service.RoleService;

@RestController
public class RolesController {

    private final RoleService roleService;

    public RolesController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<Role>>> getAllRoles() {
        return ApiResponse.of(HttpStatus.OK, "OK", roleService.findAll()).toEntity();
    }
}
