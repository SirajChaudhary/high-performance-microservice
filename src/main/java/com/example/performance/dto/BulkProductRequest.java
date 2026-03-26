package com.example.performance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/*
=====================================================
DTO: BulkProductRequest

PURPOSE:
- Represents input data for bulk product creation
- Used in APIs to accept multiple products in a single request

PERFORMANCE:
- Reduces number of HTTP calls
- Minimizes network overhead
- Enables batch processing for better throughput
- Validation ensures data correctness before processing
=====================================================
*/
public record BulkProductRequest(
        @NotEmpty(message = "Products list cannot be empty")
        @Valid
        List<ProductRequest> products
) {}