package com.kiprin.messages.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kiprin.common.config.OpenApiSupport;

import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI messagesOpenApi() {
        return OpenApiSupport.bearerOpenApi(
                "Messages API",
                "Messages, Subscriptions, and Feed Service",
                "0.0.1");
    }
}
