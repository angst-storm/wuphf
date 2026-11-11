package com.kiprin.ums.web;

import org.springframework.web.server.ServerWebExchange;

import com.kiprin.ums.config.SessionAuthFilter;
import com.kiprin.ums.domain.Session;
import com.kiprin.ums.service.UnauthorizedException;

public final class CurrentSession {

    private CurrentSession() {
    }

    public static Session require(ServerWebExchange exchange) {
        Object value = exchange.getAttribute(SessionAuthFilter.ATTR_SESSION);
        if (!(value instanceof Session session)) {
            throw new UnauthorizedException("Not authenticated");
        }
        return session;
    }
}
