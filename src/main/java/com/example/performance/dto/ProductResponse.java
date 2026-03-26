package com.example.performance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
=====================================================
DTO: ProductResponse

PURPOSE:
- Represents complete product details

PERFORMANCE:
- Java Record (immutable data structure)
- Reduces boilerplate (no getters/setters)
- Lower memory footprint than traditional POJOs
- Faster serialization/deserialization (better for APIs)
- Ideal for read-heavy operations
=====================================================
*/
public record ProductResponse(

        Long id,
        String name,
        String category,
        BigDecimal price,
        Integer stock,
        LocalDateTime createdAt
) {}