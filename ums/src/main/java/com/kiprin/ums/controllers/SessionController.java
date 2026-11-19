package com.kiprin.ums.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kiprin.ums.domain.Session;
import com.kiprin.ums.dto.LoginRequest;
import com.kiprin.ums.dto.SessionIdResponse;
import com.kiprin.ums.dto.SessionOpenedResponse;
import com.kiprin.ums.exception.NotFoundException;
import com.kiprin.ums.service.SessionService;
import com.kiprin.ums.web.ApiResponse;
import com.kiprin.ums.web.SessionAuthFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<SessionOpenedResponse>> openSession(@Valid @RequestBody LoginRequest login) {
        Session session = sessionService.open(login);
        return ApiResponse.of(
                HttpStatus.CREATED,
                "Session opened",
                new SessionOpenedResponse(session.getId(), session.getUserId())).toEntity();
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<SessionIdResponse>> closeSession(
            HttpServletRequest request,
            @PathVariable UUID sessionId) {
        UUID actorId = SessionAuthFilter.getSession(request).getUserId();
        Session session = sessionService.close(actorId, sessionId);
        return ApiResponse.of(HttpStatus.OK, "Session closed", new SessionIdResponse(session.getId())).toEntity();
    }

    @GetMapping("/user/{userId}/last")
    public ResponseEntity<ApiResponse<Session>> getLastSession(
            HttpServletRequest request,
            @PathVariable UUID userId) {
        UUID actorId = SessionAuthFilter.getSession(request).getUserId();
        Session session = sessionService.findLastForUser(actorId, userId)
                .orElseThrow(() -> new NotFoundException("No session found for user"));
        return ApiResponse.of(HttpStatus.OK, "OK", session).toEntity();
    }
}
