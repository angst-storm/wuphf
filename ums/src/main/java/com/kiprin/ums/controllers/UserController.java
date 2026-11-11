package com.kiprin.ums.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.kiprin.ums.domain.User;
import com.kiprin.ums.service.UserService;
import com.kiprin.ums.web.ApiResponse;
import com.kiprin.ums.web.CurrentSession;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Mono<ResponseEntity<Map<String, Object>>> getAllUsers(ServerWebExchange exchange) {
        return ApiResponse.okAsync(() -> {
            UUID actorId = CurrentSession.require(exchange).getUserId();
            return ApiResponse.of(200, "OK", userService.findAll(actorId));
        });
    }

    @GetMapping("/user/{userId}")
    public Mono<ResponseEntity<Map<String, Object>>> getUser(@PathVariable UUID userId) {
        return ApiResponse.okAsync(() -> userService.findById(userId)
                .map(user -> ApiResponse.of(200, "OK", user))
                .orElseGet(() -> ApiResponse.of(404, "User not found", null)));
    }

    @PostMapping("/user")
    public Mono<ResponseEntity<Map<String, Object>>> createUser(@RequestBody User user) {
        return ApiResponse.okAsync(() -> ApiResponse.of(201, "Created", userService.create(user)));
    }

    @PutMapping("/user/{userId}")
    public Mono<ResponseEntity<Map<String, Object>>> updateUser(
            ServerWebExchange exchange,
            @PathVariable UUID userId,
            @RequestBody User user) {
        return ApiResponse.okAsync(() -> {
            UUID actorId = CurrentSession.require(exchange).getUserId();
            return ApiResponse.of(200, "Updated", userService.update(actorId, userId, user));
        });
    }

    @DeleteMapping("/user/{userId}")
    public Mono<ResponseEntity<Map<String, Object>>> deleteUser(
            ServerWebExchange exchange,
            @PathVariable UUID userId) {
        return ApiResponse.okAsync(() -> {
            UUID actorId = CurrentSession.require(exchange).getUserId();
            userService.delete(actorId, userId);
            return ApiResponse.of(200, "Deleted", Map.of("userId", userId.toString()));
        });
    }
}
