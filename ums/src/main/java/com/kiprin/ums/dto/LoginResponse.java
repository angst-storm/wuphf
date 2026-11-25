package com.kiprin.ums.dto;

import java.util.UUID;

public record LoginResponse(UUID userId, String accessToken) {
}
