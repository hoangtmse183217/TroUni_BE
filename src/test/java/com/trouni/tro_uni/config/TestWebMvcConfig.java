package com.trouni.tro_uni.config;

import com.trouni.tro_uni.service.EmailVerificationService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.mockito.Mockito;

@TestConfiguration
public class TestWebMvcConfig {

    @Bean
    @Primary
    public EmailVerificationService emailVerificationService() {
        return Mockito.mock(EmailVerificationService.class);
    }
}
