package com.kameleoon.test.config;

import com.kameleoon.test.service.QuoteService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class QuoteControllerTestConfig {
    @Bean
    public QuoteService userService() {
        return mock(QuoteService.class);
    }
}
