package com.kiprin.ums.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kiprin.ums.web.SessionAuthFilter;
import com.kiprin.ums.domain.User;
import com.kiprin.ums.dto.PatchUserRequest;
import com.kiprin.ums.dto.UserIdResponse;
import com.kiprin.ums.exception.NotFoundException;
import com.kiprin.ums.service.UserService;
import com.kiprin.ums.web.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers(HttpServletRequest request) {
        UUID actorId = SessionAuthFilter.getSession(request).getUserId();
        return ApiResponse.of(HttpStatus.OK, "OK", userService.findAll(actorId)).toEntity();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<User>> getUser(@PathVariable UUID userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return ApiResponse.of(HttpStatus.OK, "OK", user).toEntity();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody User user) {
        return ApiResponse.of(HttpStatus.CREATED, "Created", userService.create(user)).toEntity();
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            HttpServletRequest request,
            @PathVariable UUID userId,
            @Valid @RequestBody User user) {
        UUID actorId = SessionAuthFilter.getSession(request).getUserId();
        return ApiResponse.of(HttpStatus.OK, "Updated", userService.update(actorId, userId, user)).toEntity();
    }

    @PatchMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<User>> patchUser(
            HttpServletRequest request,
            @PathVariable UUID userId,
            @Valid @RequestBody PatchUserRequest patch) {
        UUID actorId = SessionAuthFilter.getSession(request).getUserId();
        return ApiResponse.of(HttpStatus.OK, "Patched", userService.patch(actorId, userId, patch)).toEntity();
    }

    @PostMapping("/user/{userId}/role/{roleId}")
    public ResponseEntity<ApiResponse<User>> assignRole(
            HttpServletRequest request,
            @PathVariable UUID userId,
            @PathVariable UUID roleId) {
        UUID actorId = SessionAuthFilter.getSession(request).getUserId();
        return ApiResponse.of(
                HttpStatus.OK,
                "Role assigned",
                userService.assignRole(actorId, userId, roleId)).toEntity();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<UserIdResponse>> deleteUser(
            HttpServletRequest request,
            @PathVariable UUID userId) {
        UUID actorId = SessionAuthFilter.getSession(request).getUserId();
        userService.delete(actorId, userId);
        return ApiResponse.of(HttpStatus.OK, "Deleted", new UserIdResponse(userId)).toEntity();
    }
}
