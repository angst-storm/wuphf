package com.kiprin.ums.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.event.AfterConvertCallback;

import com.kiprin.ums.domain.Session;
import com.kiprin.ums.domain.User;

@Configuration
public class JdbcConfig {

    @Bean
    AfterConvertCallback<User> userAfterConvert() {
        return user -> {
            user.markNotNew();
            return user;
        };
    }

    @Bean
    AfterConvertCallback<Session> sessionAfterConvert() {
        return session -> {
            session.markNotNew();
            return session;
        };
    }
}
