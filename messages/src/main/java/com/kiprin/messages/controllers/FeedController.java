package com.kiprin.messages.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kiprin.common.auth.AuthPrincipal;
import com.kiprin.common.web.ApiResponse;
import com.kiprin.messages.domain.Message;
import com.kiprin.messages.service.FeedService;

@RestController
@RequestMapping("/feed")
public class FeedController {

    private final FeedService feedService;

    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Message>>> getFeed(
            @AuthenticationPrincipal AuthPrincipal actor,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return ApiResponse.of(HttpStatus.OK, "OK", feedService.getFeed(actor, limit, offset)).toEntity();
    }
}
