package com.kameleoon.test.config;

import com.kameleoon.test.service.UserService;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

public class UserControllerTestConfig {
    @Bean
    public UserService userService() {
        return mock(UserService.class);
    }
}
