package com.example.performance.config;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.server.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

/*
=====================================================
CORRELATION ID FILTER (REQUEST TRACING)

WHAT THIS DOES:
- Generates or propagates a correlation ID per request
- Adds it to response headers
- Makes logs traceable across the entire request flow

HOW IT WORKS:
- Reads correlation ID from incoming request (if present)
- Otherwise generates a new UUID
- Stores ID in Reactor Context and MDC for logging

PERFORMANCE:
- Lightweight (UUID generation is minimal overhead)
- Non-blocking (fully reactive, no thread blocking)
- Improves debugging without impacting throughput

USE CASE:
- Distributed systems and microservices
- Debugging and tracing request flow across services
=====================================================
*/
@Component
public class CorrelationIdFilter implements WebFilter {

    private static final String CORRELATION_ID = "correlationId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String correlationId = getOrCreateCorrelationId(exchange);

        // Add correlation ID to response headers
        exchange.getResponse().getHeaders().add(CORRELATION_ID, correlationId);

        return chain.filter(exchange)

                // Store in Reactor Context (preferred in reactive systems)
                .contextWrite(ctx -> ctx.put(CORRELATION_ID, correlationId))

                // Add to MDC for logging frameworks
                .doOnEach(signal -> {
                    if (!signal.isOnComplete()) {
                        MDC.put(CORRELATION_ID, correlationId);
                    }
                })

                // Clean up MDC after request completes
                .doFinally(signal -> MDC.remove(CORRELATION_ID));
    }

    private String getOrCreateCorrelationId(ServerWebExchange exchange) {

        String existing = exchange.getRequest()
                .getHeaders()
                .getFirst(CORRELATION_ID);

        return existing != null ? existing : UUID.randomUUID().toString();
    }
}