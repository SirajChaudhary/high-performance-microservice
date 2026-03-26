package com.example.performance.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/*
=====================================================
REACTIVE LOGGING UTILITY

WHAT THIS DOES:
- Logs start, success, error
- Detects slow APIs based on threshold

PERFORMANCE:
- Fully reactive-safe (uses defer)
- Non-blocking logging
- Helps identify bottlenecks
=====================================================
*/
@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveLoggingUtil {

    @Value("${app.logging.slow-api-threshold:500}")
    private long slowApiThreshold;

    /*
    =====================================================
    MONO LOGGING
    =====================================================
    */
    public <T> Mono<T> logMono(Mono<T> mono, String operation) {

        return Mono.defer(() -> {

            long start = System.currentTimeMillis();

            return mono
                    .doOnSubscribe(sub ->
                            log.info("START → {}", operation))

                    .doOnSuccess(res -> {
                        long duration = System.currentTimeMillis() - start;

                        if (duration > slowApiThreshold) {
                            log.warn("SLOW API → {} ({} ms)", operation, duration);
                        } else {
                            log.info("SUCCESS → {} ({} ms)", operation, duration);
                        }
                    })

                    .doOnError(error ->
                            log.error("ERROR → {} | message={}",
                                    operation, error.getMessage()));
        });
    }

    /*
    =====================================================
    FLUX LOGGING
    =====================================================
    */
    public <T> Flux<T> logFlux(Flux<T> flux, String operation) {

        return Flux.defer(() -> {

            long start = System.currentTimeMillis();

            return flux
                    .doOnSubscribe(sub ->
                            log.info("START → {}", operation))

                    .doOnComplete(() -> {
                        long duration = System.currentTimeMillis() - start;

                        if (duration > slowApiThreshold) {
                            log.warn("SLOW API → {} ({} ms)", operation, duration);
                        } else {
                            log.info("SUCCESS → {} ({} ms)", operation, duration);
                        }
                    })

                    .doOnError(error ->
                            log.error("ERROR → {} | message={}",
                                    operation, error.getMessage()));
        });
    }
}