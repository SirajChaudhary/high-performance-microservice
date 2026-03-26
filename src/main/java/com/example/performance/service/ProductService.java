package com.example.performance.service;

import com.example.performance.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/*
=====================================================
SERVICE: ProductService

PURPOSE:
- Defines business operations for product management
- Acts as abstraction between controller and repository layers

PERFORMANCE:
- Enables loose coupling and better scalability
- Improves testability and separation of concerns
=====================================================
*/
public interface ProductService {

    /*
    =====================================================
    CREATE PRODUCT

    PERFORMANCE:
    - Non-blocking execution using Mono
    =====================================================
    */
    Mono<ProductResponse> createProduct(ProductRequest request);

    /*
    =====================================================
    GET PRODUCT BY ID

    PERFORMANCE:
    - Works with caching to reduce DB calls
    =====================================================
    */
    Mono<ProductResponse> getProductById(Long id);

    /*
    =====================================================
    GET ALL PRODUCTS

    PERFORMANCE:
    - Uses Flux for streaming large datasets
    =====================================================
    */
    Flux<ProductResponse> getAllProducts();

    /*
    =====================================================
    SEARCH PRODUCTS

    PERFORMANCE:
    - Delegates filtering to DB (avoids in-memory processing)
    =====================================================
    */
    Flux<ProductResponse> searchProducts(String name);

    /*
    =====================================================
    FILTER BY CATEGORY

    PERFORMANCE:
    - DB-level filtering for better performance
    =====================================================
    */
    Flux<ProductResponse> getProductsByCategory(String category);

    /*
    =====================================================
    OFFSET PAGINATION

    PERFORMANCE:
    - Simple pagination approach
    - Less efficient for large datasets
    =====================================================
    */
    Flux<ProductResponse> getProducts(int limit, int offset);

    /*
    =====================================================
    KEYSET PAGINATION

    PERFORMANCE:
    - Uses indexed cursor (createdAt)
    - More efficient for large datasets
    =====================================================
    */
    Flux<ProductResponse> getNextPage(LocalDateTime lastCreatedAt, int limit);

    /*
    =====================================================
    PRODUCT SUMMARY (AGGREGATION)

    PERFORMANCE:
    - Parallel execution using reactive streams
    - Non-blocking aggregation
    =====================================================
    */
    Mono<ProductSummaryResponse> getProductSummary(Long productId);

    /*
    =====================================================
    BULK INSERT

    PERFORMANCE:
    - Batch processing reduces DB round trips
    - Improves write throughput
    =====================================================
    */
    Flux<ProductResponse> createProductsBulk(BulkProductRequest request);

    /*
    =====================================================
    FETCH PRODUCT SUMMARIES

    PERFORMANCE:
    - Uses projection query
    - Fetches only required fields
    =====================================================
    */
    Flux<ProductSummaryLiteResponse> getProductSummaries();
}