package com.kiprin.ums.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kiprin.common.web.ApiResponse;
import com.kiprin.ums.dto.LoginRequest;
import com.kiprin.ums.dto.LoginResponse;
import com.kiprin.ums.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest login) {
        return ApiResponse.of(HttpStatus.OK, "Logged in", authService.login(login)).toEntity();
    }
}
