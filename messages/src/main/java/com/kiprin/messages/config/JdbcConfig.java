package com.kiprin.messages.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.event.AfterConvertCallback;

import com.kiprin.messages.domain.Message;

@Configuration
public class JdbcConfig {

    @Bean
    AfterConvertCallback<Message> messageAfterConvert() {
        return message -> {
            message.markNotNew();
            return message;
        };
    }
}
