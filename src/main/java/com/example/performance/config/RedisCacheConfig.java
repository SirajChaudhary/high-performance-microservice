package com.example.performance.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;

/*
=====================================================
CONFIG: RedisCacheConfig

PURPOSE:
- Configures Reactive Redis for caching
- Provides ReactiveRedisTemplate for cache operations

PERFORMANCE:
- Fully non-blocking I/O (WebFlux compatible)
- Efficient JSON serialization
- Optimized for high-throughput caching
=====================================================
*/
@Configuration
public class RedisCacheConfig {

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory factory) {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        RedisSerializationContext<String, Object> context =
                RedisSerializationContext
                        .<String, Object>newSerializationContext(new StringRedisSerializer())
                        .value(new GenericJackson2JsonRedisSerializer(objectMapper))
                        .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}