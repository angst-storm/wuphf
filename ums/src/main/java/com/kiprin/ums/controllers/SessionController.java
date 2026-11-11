package com.kiprin.ums.controllers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.kiprin.ums.dto.LoginRequest;
import com.kiprin.ums.service.SessionService;
import com.kiprin.ums.web.ApiResponse;
import com.kiprin.ums.web.CurrentSession;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, Object>>> openSession(@RequestBody LoginRequest login) {
        return ApiResponse.okAsync(() -> {
            var session = sessionService.open(login);
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("sessionId", session.getId().toString());
            data.put("userId", session.getUserId().toString());
            return ApiResponse.of(201, "Session opened", data);
        });
    }

    @DeleteMapping("/{sessionId}")
    public Mono<ResponseEntity<Map<String, Object>>> closeSession(
            ServerWebExchange exchange,
            @PathVariable UUID sessionId) {
        return ApiResponse.okAsync(() -> {
            UUID actorId = CurrentSession.require(exchange).getUserId();
            var session = sessionService.close(actorId, sessionId);
            return ApiResponse.of(200, "Session closed", Map.of("sessionId", session.getId().toString()));
        });
    }

    @GetMapping("/user/{userId}/last")
    public Mono<ResponseEntity<Map<String, Object>>> getLastSession(
            ServerWebExchange exchange,
            @PathVariable UUID userId) {
        return ApiResponse.okAsync(() -> {
            UUID actorId = CurrentSession.require(exchange).getUserId();
            return sessionService.findLastForUser(actorId, userId)
                    .map(session -> ApiResponse.of(200, "OK", session))
                    .orElseGet(() -> ApiResponse.of(404, "No session found for user", null));
        });
    }
}
