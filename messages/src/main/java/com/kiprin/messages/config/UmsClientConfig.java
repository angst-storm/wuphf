package com.kiprin.messages.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.web.client.RestClient;

@Configuration
public class UmsClientConfig {

    @Bean
    public RestClient umsRestClient(@Value("${ums.base-url}") String umsBaseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        return RestClient.builder()
                .baseUrl(umsBaseUrl)
                .requestFactory(factory)
                .requestInterceptor(propagateBearerToken())
                .build();
    }

    /** Forwards the caller's JWT to UMS so the downstream call is authenticated as the same user. */
    private static ClientHttpRequestInterceptor propagateBearerToken() {
        return (request, body, execution) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication instanceof AbstractOAuth2TokenAuthenticationToken<?> bearer) {
                request.getHeaders().setBearerAuth(bearer.getToken().getTokenValue());
            }
            return execution.execute(request, body);
        };
    }
}
