package com.kiprin.ums.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI umsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("UMS API")
                        .description("User Management Service")
                        .version("0.0.1"));
    }
}
