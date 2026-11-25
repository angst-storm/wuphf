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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kiprin.common.auth.AuthPrincipal;
import com.kiprin.common.web.ApiResponse;
import com.kiprin.messages.domain.Subscription;
import com.kiprin.messages.dto.SubscriptionDeletedResponse;
import com.kiprin.messages.service.SubscriptionService;

@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/{followeeId}")
    public ResponseEntity<ApiResponse<Subscription>> subscribe(
            @AuthenticationPrincipal AuthPrincipal actor,
            @PathVariable UUID followeeId) {
        Subscription subscription = subscriptionService.subscribe(actor, followeeId);
        return ApiResponse.of(HttpStatus.CREATED, "Subscribed", subscription).toEntity();
    }

    @DeleteMapping("/{followeeId}")
    public ResponseEntity<ApiResponse<SubscriptionDeletedResponse>> unsubscribe(
            @AuthenticationPrincipal AuthPrincipal actor,
            @PathVariable UUID followeeId) {
        subscriptionService.unsubscribe(actor, followeeId);
        return ApiResponse.of(
                HttpStatus.OK,
                "Unsubscribed",
                new SubscriptionDeletedResponse(actor.userId(), followeeId)).toEntity();
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<ApiResponse<List<Subscription>>> getFollowing(@PathVariable UUID userId) {
        return ApiResponse.of(HttpStatus.OK, "OK", subscriptionService.findFollowing(userId)).toEntity();
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<ApiResponse<List<Subscription>>> getFollowers(@PathVariable UUID userId) {
        return ApiResponse.of(HttpStatus.OK, "OK", subscriptionService.findFollowers(userId)).toEntity();
    }
}
