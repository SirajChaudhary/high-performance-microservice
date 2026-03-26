package com.example.performance.dto;

import java.math.BigDecimal;

/*
=====================================================
DTO: ProductSummaryLiteResponse

PURPOSE:
- Represents lightweight product data
- Used in listing or summary APIs where only key fields are required (e.g., name, price)
- Optimized for smaller payload and faster response

PERFORMANCE:
- Java Record (immutable data structure)
- Reduces boilerplate (no getters/setters)
- Fetches only required fields (name, price)
- Smaller payload size
- Faster serialization and response time
=====================================================
*/
public record ProductSummaryLiteResponse(
        String name,
        BigDecimal price
) {}