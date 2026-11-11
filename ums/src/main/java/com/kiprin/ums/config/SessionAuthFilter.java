package com.kiprin.ums.config;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiprin.ums.service.SessionService;
import com.kiprin.ums.service.UnauthorizedException;
import com.kiprin.ums.web.ApiResponse;
import com.kiprin.ums.web.SessionHeaders;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@Order(-100)
public class SessionAuthFilter implements WebFilter {

    public static final String ATTR_SESSION = "ums.session";

    private final SessionService sessionService;
    private final ObjectMapper objectMapper;

    public SessionAuthFilter(SessionService sessionService, ObjectMapper objectMapper) {
        this.sessionService = sessionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        if (isPublic(request)) {
            return chain.filter(exchange);
        }

        String rawSessionId = request.getHeaders().getFirst(SessionHeaders.SESSION_ID);
        if (rawSessionId == null || rawSessionId.isBlank()) {
            return unauthorized(exchange, "Missing " + SessionHeaders.SESSION_ID + " header");
        }

        final UUID sessionId;
        try {
            sessionId = UUID.fromString(rawSessionId.trim());
        } catch (IllegalArgumentException e) {
            return unauthorized(exchange, "Invalid " + SessionHeaders.SESSION_ID + " header");
        }

        return Mono.fromCallable(() -> sessionService.requireActive(sessionId))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(session -> {
                    exchange.getAttributes().put(ATTR_SESSION, session);
                    return chain.filter(exchange);
                })
                .onErrorResume(UnauthorizedException.class, e -> unauthorized(exchange, e.getMessage()));
    }

    private static boolean isPublic(ServerHttpRequest request) {
        if (request.getMethod() == HttpMethod.OPTIONS) {
            return true;
        }
        String path = request.getPath().pathWithinApplication().value();
        if (request.getMethod() == HttpMethod.POST && "/sessions/login".equals(path)) {
            return true;
        }
        return request.getMethod() == HttpMethod.POST && "/users/user".equals(path);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(ApiResponse.of(401, message, null));
        } catch (JsonProcessingException e) {
            bytes = ("{\"code\":401,\"message\":\"" + message + "\",\"data\":null}")
                    .getBytes(StandardCharsets.UTF_8);
        }
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
