package com.example.performance.dto;

import java.math.BigDecimal;

/*
=====================================================
DTO: ProductSummaryResponse

PURPOSE:
- Represents aggregated product data (summary view)
- Used in APIs combining product, order count, and revenue

PERFORMANCE:
- Java Record (immutable data structure)
- Reduces boilerplate (no getters/setters)
- Lightweight DTO for aggregated responses
- Faster serialization/deserialization
- Efficient for read-heavy and analytics APIs
=====================================================
*/
public record ProductSummaryResponse(

        Long productId,
        String name,
        String category,
        BigDecimal price,
        Long totalOrders,
        BigDecimal totalRevenue
) {}