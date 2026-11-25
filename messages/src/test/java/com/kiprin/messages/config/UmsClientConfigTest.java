package com.kiprin.messages.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import com.kiprin.common.auth.AuthPrincipal;
import com.kiprin.common.auth.RoleName;
import com.kiprin.common.security.JwtAuthToken;
import com.kiprin.messages.client.UmsClient;
import com.sun.net.httpserver.HttpServer;

class UmsClientConfigTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void callerJwtIsForwardedToUms() throws IOException {
        AtomicReference<String> authorization = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            authorization.set(exchange.getRequestHeaders().getFirst("Authorization"));
            byte[] body = "{\"code\":200,\"message\":\"OK\",\"data\":{\"id\":\"x\"}}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        try {
            UmsClient client = new UmsClient(new UmsClientConfig()
                    .umsRestClient("http://127.0.0.1:" + server.getAddress().getPort()));

            Jwt jwt = Jwt.withTokenValue("caller-token")
                    .header("alg", "HS256")
                    .subject(UUID.randomUUID().toString())
                    .issuedAt(Instant.now())
                    .expiresAt(Instant.now().plusSeconds(60))
                    .build();
            AuthPrincipal principal = new AuthPrincipal(UUID.randomUUID(), Set.of(RoleName.SUBSCRIBER));
            SecurityContextHolder.getContext().setAuthentication(
                    new JwtAuthToken(jwt, principal, List.of(new SimpleGrantedAuthority("ROLE_SUBSCRIBER"))));

            assertThat(client.userExists(UUID.randomUUID())).isTrue();
            assertThat(authorization.get()).isEqualTo("Bearer caller-token");
        } finally {
            server.stop(0);
        }
    }
}
