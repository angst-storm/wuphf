package com.kiprin.ums.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RoleName {
    ADMIN,
    PRODUCER,
    SUBSCRIBER;

    @JsonCreator
    public static RoleName from(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return RoleName.valueOf(value.trim().toUpperCase());
    }
}
