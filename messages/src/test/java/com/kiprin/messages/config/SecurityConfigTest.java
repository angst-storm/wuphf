package com.kiprin.messages.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.kiprin.common.auth.AuthPrincipal;
import com.kiprin.common.auth.JwtProperties;
import com.kiprin.common.auth.JwtService;
import com.kiprin.common.auth.RoleName;
import com.kiprin.common.config.CommonAutoConfiguration;
import com.kiprin.messages.client.UmsClient;
import com.kiprin.messages.controllers.FeedController;
import com.kiprin.messages.domain.Message;
import com.kiprin.messages.service.FeedService;

@WebMvcTest(controllers = FeedController.class)
@Import({CommonAutoConfiguration.class, SecurityConfig.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtProperties jwtProperties;

    @MockitoBean
    private FeedService feedService;

    @MockitoBean
    private UmsClient umsClient;

    @Test
    void missingBearerReturnsApi401() throws Exception {
        mockMvc.perform(get("/feed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
        verifyNoInteractions(feedService, umsClient);
    }

    @Test
    void expiredTokenReturnsApi401() throws Exception {
        JwtService pastIssuer = new JwtService(
                jwtProperties,
                Clock.fixed(Instant.parse("2020-01-01T00:00:00Z"), ZoneOffset.UTC));
        String token = pastIssuer.issue(UUID.randomUUID(), List.of(RoleName.SUBSCRIBER));

        mockMvc.perform(get("/feed").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
        verifyNoInteractions(feedService, umsClient);
    }

    @Test
    void validJwtAuthenticatesLocallyWithoutCallingUms() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = jwtService.issue(userId, List.of(RoleName.SUBSCRIBER));

        Message m = new Message();
        m.setId(UUID.randomUUID());
        m.setAuthorId(UUID.randomUUID());
        m.setText("hello");
        AuthPrincipal expected = new AuthPrincipal(userId, Set.of(RoleName.SUBSCRIBER));
        when(feedService.getFeed(argThat(expected::equals), eq(50), eq(0))).thenReturn(List.of(m));

        mockMvc.perform(get("/feed").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].text").value("hello"));

        verifyNoInteractions(umsClient);
    }

    @Test
    void rawJwtSurvivesAuthenticationForForwardingToUms() throws Exception {
        UUID userId = UUID.randomUUID();
        String token = jwtService.issue(userId, List.of(RoleName.SUBSCRIBER));
        AtomicReference<String> seenInController = new AtomicReference<>();
        when(feedService.getFeed(any(), eq(50), eq(0))).thenAnswer(invocation -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof AbstractOAuth2TokenAuthenticationToken<?> bearer) {
                seenInController.set(bearer.getToken().getTokenValue());
            }
            return List.of();
        });

        mockMvc.perform(get("/feed").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(jsonPath("$.code").value(200));

        assertThat(seenInController.get()).isEqualTo(token);
    }
}
