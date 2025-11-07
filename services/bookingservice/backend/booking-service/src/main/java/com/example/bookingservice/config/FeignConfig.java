package com.example.bookingservice.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    // Configures Feign to log requests and responses (useful for debugging inter-service calls)
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL; // Can be NONE, BASIC, HEADERS, FULL
    }

    // You can also define custom error decoders, request interceptors here for Feign
}