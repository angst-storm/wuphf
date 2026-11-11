package com.kiprin.ums.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.kiprin.ums.dto.PatchUserRequest;
import com.kiprin.ums.service.UserService;
import com.kiprin.ums.web.ApiResponse;
import com.kiprin.ums.web.CurrentSession;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserRolesController {

    private final UserService userService;

    public UserRolesController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/{userId}/role/{roleId}")
    public Mono<ResponseEntity<Map<String, Object>>> assignRole(
            ServerWebExchange exchange,
            @PathVariable UUID userId,
            @PathVariable UUID roleId) {
        return ApiResponse.okAsync(() -> {
            UUID actorId = CurrentSession.require(exchange).getUserId();
            return ApiResponse.of(200, "Role assigned", userService.assignRole(actorId, userId, roleId));
        });
    }

    @PatchMapping("/user/{userId}")
    public Mono<ResponseEntity<Map<String, Object>>> patchUser(
            ServerWebExchange exchange,
            @PathVariable UUID userId,
            @RequestBody PatchUserRequest patch) {
        return ApiResponse.okAsync(() -> {
            UUID actorId = CurrentSession.require(exchange).getUserId();
            return ApiResponse.of(200, "Patched", userService.patch(actorId, userId, patch));
        });
    }
}
