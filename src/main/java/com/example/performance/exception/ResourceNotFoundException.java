package com.example.performance.exception;

/*
=====================================================
CUSTOM EXCEPTION: Resource Not Found

PERFORMANCE:
- Avoids misuse of generic exceptions
- Improves clarity and control of error handling
=====================================================
*/
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}