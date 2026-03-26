package com.example.performance.cache;

/*
=====================================================
UTILITY: CacheKeys

PURPOSE:
- Centralizes cache key generation
- Avoids hardcoded strings across the application

PERFORMANCE:
- Ensures consistent key usage
- Prevents cache duplication issues
=====================================================
*/
public class CacheKeys {

    private CacheKeys() {
        // Prevent instantiation
    }

    public static String productById(Long id) {
        return "product:" + id;
    }
}