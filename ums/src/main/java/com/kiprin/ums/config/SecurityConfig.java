package com.kiprin.ums.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.kiprin.common.security.JwtSecurity;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtSecurity jwtSecurity) throws Exception {
        return jwtSecurity.build(http, auth -> auth
                .requestMatchers(HttpMethod.POST, "/auth/login", "/users").permitAll());
    }
}
