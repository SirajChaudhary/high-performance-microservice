package com.example.performance.exception;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/*
=====================================================
GLOBAL EXCEPTION HANDLER (WebFlux)

WHAT THIS DOES:
- Handles all exceptions centrally
- Returns consistent and structured error responses

PERFORMANCE:
- Avoids repetitive try-catch blocks
- Improves maintainability and readability
=====================================================
*/
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
    =====================================================
    HANDLE: Validation Errors (@Valid)
    =====================================================
    */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidation(WebExchangeBindException ex) {

        String message = ex.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        ErrorResponse error = new ErrorResponse(
                "VALIDATION_ERROR",
                message,
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    /*
    =====================================================
    HANDLE: Bad Request (Illegal Arguments)
    =====================================================
    */
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleBadRequest(IllegalArgumentException ex) {

        ErrorResponse error = new ErrorResponse(
                "BAD_REQUEST",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now()
        );

        return Mono.just(ResponseEntity.badRequest().body(error));
    }

    /*
    =====================================================
    HANDLE: Resource Not Found
    =====================================================
    */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(ResourceNotFoundException ex) {

        ErrorResponse error = new ErrorResponse(
                "NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    /*
    =====================================================
    HANDLE: Generic Exception
    =====================================================
    */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGeneric(Exception ex) {

        ErrorResponse error = new ErrorResponse(
                "INTERNAL_ERROR",
                "Something went wrong",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }
}