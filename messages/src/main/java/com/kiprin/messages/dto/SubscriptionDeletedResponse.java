package com.kiprin.messages.dto;

import java.util.UUID;

public record SubscriptionDeletedResponse(UUID followerId, UUID followeeId) {
}
