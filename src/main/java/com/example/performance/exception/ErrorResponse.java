package com.example.performance.exception;

import java.time.LocalDateTime;

/*
=====================================================
ERROR RESPONSE DTO

PERFORMANCE:
- Lightweight immutable object
- Consistent error structure across APIs
=====================================================
*/
public record ErrorResponse(
        String code,
        String message,
        int status,
        LocalDateTime timestamp
) {}