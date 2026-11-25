package com.kiprin.common.auth;

import java.util.Set;
import java.util.UUID;

/**
 * Authenticated caller extracted from a verified JWT. Inject into controllers with
 * {@code @AuthenticationPrincipal AuthPrincipal actor}.
 */
public record AuthPrincipal(UUID userId, Set<RoleName> roles) {

    public AuthPrincipal {
        roles = roles == null ? Set.of() : Set.copyOf(roles);
    }

    public boolean hasRole(RoleName role) {
        return role != null && roles.contains(role);
    }

    public boolean hasAnyRole(RoleName... allowed) {
        if (allowed == null) {
            return false;
        }
        for (RoleName role : allowed) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAdmin() {
        return hasRole(RoleName.ADMIN);
    }
}
