package com.example.performance.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/*
=====================================================
DTO: ProductRequest

PURPOSE:
- Represents input data for creating or updating a product
- Used in API requests to capture user input

PERFORMANCE:
- Java Record (immutable data structure)
- Reduces boilerplate (no getters/setters)
- Lightweight and efficient for request handling
- Early validation prevents unnecessary processing and DB calls
=====================================================
*/
public record ProductRequest(

        @NotBlank(message = "Name cannot be empty")
        String name,

        @NotBlank(message = "Category cannot be empty")
        String category,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotNull(message = "Stock is required")
        @Min(value = 0, message = "Stock cannot be negative")
        Integer stock
) {}