package com.kiprin.messages.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kiprin.common.auth.AuthPrincipal;
import com.kiprin.common.auth.RoleName;
import com.kiprin.common.exception.BadRequestException;
import com.kiprin.common.exception.ForbiddenException;
import com.kiprin.common.exception.NotFoundException;
import com.kiprin.messages.client.UmsClient;
import com.kiprin.messages.domain.Subscription;
import com.kiprin.messages.repository.SubscriptionRepository;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UmsClient umsClient;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, UmsClient umsClient) {
        this.subscriptionRepository = subscriptionRepository;
        this.umsClient = umsClient;
    }

    @Transactional
    public Subscription subscribe(AuthPrincipal actor, UUID followeeId) {
        requireSubscriber(actor);
        if (actor.userId().equals(followeeId)) {
            throw new BadRequestException("Cannot subscribe to yourself");
        }
        if (!umsClient.userExists(followeeId)) {
            throw new NotFoundException("User not found");
        }
        return subscriptionRepository.saveIfAbsent(actor.userId(), followeeId, LocalDateTime.now());
    }

    @Transactional
    public void unsubscribe(AuthPrincipal actor, UUID followeeId) {
        requireSubscriber(actor);
        int deleted = subscriptionRepository.delete(actor.userId(), followeeId);
        if (deleted == 0) {
            throw new NotFoundException("Subscription not found");
        }
    }

    public List<Subscription> findFollowing(UUID userId) {
        return subscriptionRepository.findFollowing(userId);
    }

    public List<Subscription> findFollowers(UUID userId) {
        return subscriptionRepository.findFollowers(userId);
    }

    private static void requireSubscriber(AuthPrincipal actor) {
        if (!actor.hasAnyRole(RoleName.SUBSCRIBER, RoleName.ADMIN)) {
            throw new ForbiddenException("SUBSCRIBER or ADMIN role required");
        }
    }
}
