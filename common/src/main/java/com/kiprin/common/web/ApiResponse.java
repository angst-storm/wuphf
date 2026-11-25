package com.kiprin.common.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public record ApiResponse<T>(int code, String message, T data) {

    public static <T> ApiResponse<T> of(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    public static <T> ApiResponse<T> of(HttpStatus status, String message, T data) {
        return of(status.value(), message, data);
    }

    public ResponseEntity<ApiResponse<T>> toEntity() {
        return ResponseEntity.status(code)
                .contentType(MediaType.APPLICATION_JSON)
                .body(this);
    }
}
