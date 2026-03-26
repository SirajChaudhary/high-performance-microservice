package com.example.performance.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

/*
=====================================================
CACHE: RedisCacheService (L2 - Distributed)

PURPOSE:
- Provides distributed caching using Redis
- Acts as second-level cache shared across instances

PERFORMANCE:
- Non-blocking operations (Reactive Redis)
- Reduces database load
- Enables horizontal scalability

NOTE:
- Redis returns generic objects (LinkedHashMap)
- ObjectMapper is used to convert to target DTO
=====================================================
*/
@Service
@RequiredArgsConstructor
public class RedisCacheService {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public <T> Mono<T> get(String key, Class<T> type) {
        return redisTemplate.opsForValue()
                .get(key)
                .map(data -> objectMapper.convertValue(data, type));
    }

    public Mono<Boolean> put(String key, Object value) {
        return redisTemplate.opsForValue()
                .set(key, value, Duration.ofMinutes(5));
    }

    public Mono<Boolean> evict(String key) {
        return redisTemplate.delete(key)
                .map(count -> count > 0);
    }
}