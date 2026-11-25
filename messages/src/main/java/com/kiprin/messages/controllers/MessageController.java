package com.kiprin.messages.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kiprin.common.auth.AuthPrincipal;
import com.kiprin.common.web.ApiResponse;
import com.kiprin.messages.domain.Message;
import com.kiprin.messages.dto.CreateMessageRequest;
import com.kiprin.messages.dto.MessageIdResponse;
import com.kiprin.messages.service.MessageService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Message>> createMessage(
            @AuthenticationPrincipal AuthPrincipal actor,
            @Valid @RequestBody CreateMessageRequest body) {
        Message created = messageService.create(actor, body);
        return ApiResponse.of(HttpStatus.CREATED, "Created", created).toEntity();
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<ApiResponse<Message>> getMessage(@PathVariable UUID messageId) {
        return ApiResponse.of(HttpStatus.OK, "OK", messageService.getById(messageId)).toEntity();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Message>>> getUserMessages(@PathVariable UUID userId) {
        return ApiResponse.of(HttpStatus.OK, "OK", messageService.findByAuthor(userId)).toEntity();
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse<MessageIdResponse>> deleteMessage(
            @AuthenticationPrincipal AuthPrincipal actor,
            @PathVariable UUID messageId) {
        messageService.delete(actor, messageId);
        return ApiResponse.of(HttpStatus.OK, "Deleted", new MessageIdResponse(messageId)).toEntity();
    }
}
