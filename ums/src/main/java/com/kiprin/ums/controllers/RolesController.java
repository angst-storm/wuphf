package com.kiprin.ums.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kiprin.ums.service.RoleService;
import com.kiprin.ums.web.ApiResponse;

import reactor.core.publisher.Mono;

@RestController
public class RolesController {

    private final RoleService roleService;

    public RolesController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles")
    public Mono<ResponseEntity<Map<String, Object>>> getAllRoles() {
        return ApiResponse.okAsync(() -> ApiResponse.of(200, "OK", roleService.findAll()));
    }
}
