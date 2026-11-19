package com.kiprin.ums.web;

import java.io.IOException;
import java.util.UUID;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiprin.ums.domain.Session;
import com.kiprin.ums.exception.UnauthorizedException;
import com.kiprin.ums.service.SessionService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(-100)
public class SessionAuthFilter extends OncePerRequestFilter {

    public static final String ATTR_SESSION = "ums.session";
    private static final String SESSION_HEADER = "X-Session-Id";

    private final SessionService sessionService;
    private final ObjectMapper objectMapper;

    public SessionAuthFilter(SessionService sessionService, ObjectMapper objectMapper) {
        this.sessionService = sessionService;
        this.objectMapper = objectMapper;
    }

    public static Session getSession(HttpServletRequest request) {
        Object value = request.getAttribute(ATTR_SESSION);
        if (!(value instanceof Session session)) {
            throw new UnauthorizedException("Not authenticated");
        }
        return session;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        if (isPublic(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String rawSessionId = request.getHeader(SESSION_HEADER);
        if (rawSessionId == null || rawSessionId.isBlank()) {
            writeUnauthorized(response, "Missing " + SESSION_HEADER + " header");
            return;
        }

        try {
            UUID sessionId = UUID.fromString(rawSessionId.trim());
            Session session = sessionService.requireActive(sessionId);
            request.setAttribute(ATTR_SESSION, session);
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException e) {
            writeUnauthorized(response, "Invalid " + SESSION_HEADER + " header");
        } catch (UnauthorizedException e) {
            writeUnauthorized(response, e.getMessage());
        }
    }

    private static boolean isPublic(HttpServletRequest request) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String path = request.getRequestURI();
        if (path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/webjars/")) {
            return true;
        }
        if (HttpMethod.POST.matches(request.getMethod()) && "/sessions/login".equals(path)) {
            return true;
        }
        return HttpMethod.POST.matches(request.getMethod()) && "/users".equals(path);
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(
                response.getOutputStream(),
                ApiResponse.of(HttpStatus.UNAUTHORIZED, message, null));
    }
}
