package com.kiprin.ums.dto;

import java.util.UUID;

public record SessionOpenedResponse(UUID sessionId, UUID userId) {
}
