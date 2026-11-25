package com.kiprin.common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.kiprin.common.auth.JwtProperties;
import com.kiprin.common.auth.JwtService;
import com.kiprin.common.security.JwtSecurityConfiguration;
import com.kiprin.common.web.ApiExceptionHandler;

@AutoConfiguration
@EnableConfigurationProperties(JwtProperties.class)
@Import({CorsConfig.class, JwtSecurityConfiguration.class})
public class CommonAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public JwtService jwtService(JwtProperties properties) {
        return new JwtService(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public ApiExceptionHandler apiExceptionHandler() {
        return new ApiExceptionHandler();
    }
}
