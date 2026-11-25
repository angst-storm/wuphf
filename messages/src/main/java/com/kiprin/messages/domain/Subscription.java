package com.kiprin.messages.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record Subscription(UUID followerId, UUID followeeId, LocalDateTime createdAt) {
}
