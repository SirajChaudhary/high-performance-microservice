package com.example.performance.config;

import com.example.performance.cache.CacheKeys;
import com.example.performance.cache.LocalCache;
import com.example.performance.cache.RedisCacheService;
import com.example.performance.dto.ProductResponse;
import com.example.performance.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/*
=====================================================
CACHE WARM-UP (Startup Optimization)

PURPOSE:
- Preloads frequently accessed data into cache at startup
- Populates both L1 (Caffeine) and L2 (Redis)

PERFORMANCE:
- Eliminates cold-start latency
- Improves response time for initial requests
- Reduces immediate database load after startup

NOTE:
- Required because caching is implemented manually (no @Cacheable)
=====================================================
*/
@Component
@RequiredArgsConstructor
public class CacheWarmup {

    private static final Logger log = LoggerFactory.getLogger(CacheWarmup.class);

    private final ProductService productService;
    private final RedisCacheService redisCache;
    private final LocalCache localCache;

    @PostConstruct
    public void warmup() {

        productService.getAllProducts()

                .flatMap(product -> {
                    String key = CacheKeys.productById(product.id());

                    // Store in Redis (L2) and Caffeine (L1)
                    return redisCache.put(key, product)
                            .doOnSuccess(v -> localCache.put(key, product))
                            .thenReturn(product);
                })

                .doOnNext(p ->
                        log.debug("Cache warmed for product: {}", p.id()))

                .doOnComplete(() ->
                        log.info("Cache warm-up completed"))

                .doOnError(error ->
                        log.error("Cache warm-up failed", error))

                .subscribe();
    }
}