package com.kiprin.ums.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kiprin.common.config.OpenApiSupport;

import io.swagger.v3.oas.models.OpenAPI;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI umsOpenApi() {
        return OpenApiSupport.bearerOpenApi("UMS API", "User Management Service", "0.0.1");
    }
}
