package com.example.performance.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.time.Duration;

/*
=====================================================
CACHE: LocalCache (L1 - In-Memory)

PURPOSE:
- Provides in-memory caching for frequently accessed data
- Acts as first-level cache before Redis

PERFORMANCE:
- Ultra-fast access (in-memory, nanosecond latency)
- Reduces Redis calls for hot data
- Improves overall response time
=====================================================
*/
@Component
public class LocalCache {

    private final Cache<String, Object> cache = Caffeine.newBuilder()
            // Prevents memory overflow
            .maximumSize(10_000)
            // Auto eviction
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();

    public <T> T get(String key) {
        return (T) cache.getIfPresent(key);
    }

    public void put(String key, Object value) {
        cache.put(key, value);
    }

    public void evict(String key) {
        cache.invalidate(key);
    }
}