package com.kiprin.messages.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kiprin.common.auth.AuthPrincipal;
import com.kiprin.common.auth.RoleName;
import com.kiprin.common.exception.BadRequestException;
import com.kiprin.common.exception.ForbiddenException;
import com.kiprin.messages.domain.Message;
import com.kiprin.messages.repository.MessageRepository;

@Service
public class FeedService {

    public static final int DEFAULT_LIMIT = 50;
    public static final int MAX_LIMIT = 100;

    private final MessageRepository messageRepository;

    public FeedService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getFeed(AuthPrincipal actor, int limit, int offset) {
        if (!actor.hasAnyRole(RoleName.SUBSCRIBER, RoleName.ADMIN)) {
            throw new ForbiddenException("SUBSCRIBER or ADMIN role required");
        }
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new BadRequestException("limit must be between 1 and " + MAX_LIMIT);
        }
        if (offset < 0) {
            throw new BadRequestException("offset must be >= 0");
        }
        return messageRepository.findFeed(actor.userId(), limit, offset);
    }
}
