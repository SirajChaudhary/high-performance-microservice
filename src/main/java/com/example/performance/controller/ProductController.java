package com.example.performance.controller;

import com.example.performance.dto.*;
import com.example.performance.service.ProductService;
import com.example.performance.util.ReactiveLoggingUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/*
=====================================================
CONTROLLER: ProductController

PERFORMANCE:

- Built using Spring WebFlux (non-blocking, reactive)
- Handles high concurrency with event-loop model
- Uses ReactiveLoggingUtil for non-blocking logging
- Delegates heavy operations to service layer

RESULT:
- Better scalability
- Efficient resource utilization
- Lower latency under load
=====================================================
*/

/*
=====================================================
@Slf4j

- Lombok annotation
- Generates logger instance
- Reduces boilerplate

PERFORMANCE:
- Lightweight logging
- Supports async logging (if configured)
=====================================================
*/
@Slf4j

@RestController
@RequestMapping("/api/v1/products")

/*
=====================================================
@RequiredArgsConstructor

- Lombok annotation that generates constructor for final fields
- Enables constructor-based dependency injection

PERFORMANCE:
- Avoids reflection-based field injection (@Autowired on fields uses reflection)
- Constructor injection happens at object creation time (faster and simpler)
- Reduces runtime overhead compared to reflection

BENEFITS:
- Promotes immutability (dependencies are final)
=====================================================
*/
@RequiredArgsConstructor

public class ProductController {

    private final ProductService productService;
    private final ReactiveLoggingUtil loggingUtil;

    /*
    =====================================================
    CONCEPT DEMONSTRATED: Reactive Non-Blocking API

    API: Create Product

    PERFORMANCE:
    - Uses Mono (non-blocking reactive execution)
    - Does not block threads while waiting for DB
    - Improves scalability under high concurrency
    - Efficient resource utilization (event-loop model)
    - Uses ReactiveLoggingUtil (non-blocking logging)
    =====================================================
    */
    @PostMapping
    public Mono<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        return loggingUtil.logMono(
                productService.createProduct(request),
                "Create Product | name=" + request.name()
        );
    }

    /*
    =====================================================
    CONCEPT DEMONSTRATED: Multi-Level Caching (L1: Caffeine + L2: Redis) in Non-Blocking Reactive System

    API: Get Product By Id

    PURPOSE:
    - Fetch product details by ID
    - Uses multi-level caching (L1 + L2)

    CACHING STRATEGY:
    - L1 Cache: Caffeine (in-memory, ultra-fast)
    - L2 Cache: Redis (distributed, shared across instances)

    PERFORMANCE:
    - Reduces database calls for frequently accessed data
    - Improves response time (L1 → L2 → DB fallback)
    - Fully non-blocking (compatible with Spring WebFlux)

    IMPORTANT:
    - Spring Cache (@Cacheable) is NOT used
    - Spring Cache is blocking and not suitable for reactive (Mono/Flux)
    - Reactive caching is implemented manually to maintain non-blocking behavior

    REDIS SERVER SETUP:
    - Make sure Redis server is running before using this API
      docker run -d -p 6379:6379 redis

    - Default configuration:
        host: localhost
        port: 6379

    NOTE:
    - If Redis server is not running, L2 cache will fail and fallback to DB
    =====================================================
    */
    @GetMapping("/{id}")
    public Mono<ProductResponse> getProductById(@PathVariable Long id) {
        return loggingUtil.logMono(
                productService.getProductById(id),
                "Get Product By Id | id=" + id
        );
    }

    /*
    =====================================================
    API: Get All Products (Streaming to handle large data efficiently)

    PERFORMANCE:
    - Applies backpressure to control data flow
    - Prevents memory overload when consumer is slower than producer

    REAL IMPACT:
    - Without backpressure:
        - Memory spikes
        - App crashes
    - With backpressure:
        - Controlled flow
        - Stable system
    =====================================================
    */
    @GetMapping
    public Flux<ProductResponse> getAllProducts() {

        return loggingUtil.logFlux(
                productService.getAllProducts()
                        .onBackpressureBuffer(
                                1000,
                                dropped -> log.warn("Dropped due to backpressure: {}", dropped)
                        ),
                "Get All Products"
        );
    }

    /*
    =====================================================
    API: Search Products by Name

    PERFORMANCE:
    - Filtering is done at DB level (avoids in-memory processing)
    - Uses ILIKE for case-insensitive search
    - Uses indexed column (name) for faster lookup

    NOTE:
    - Suitable for search use cases with partial matching
    =====================================================
    */
    @GetMapping("/search")
    public Flux<ProductResponse> searchProducts(@RequestParam String name) {
        return loggingUtil.logFlux(
                productService.searchProducts(name),
                "Search Products | name=" + name
        );
    }

    /*
    =====================================================
    API: Filter Products by Category

    WHAT THIS API DOES:
    - Fetches products for a given category

    PERFORMANCE:
    - DB-level filtering (faster than in-memory filtering)
    - Index on category improves query performance

    NOTE:
    - Efficient for exact match filtering
    =====================================================
    */
    @GetMapping("/category")
    public Flux<ProductResponse> getByCategory(@RequestParam String category) {
        return loggingUtil.logFlux(
                productService.getProductsByCategory(category),
                "Get Products By Category | category=" + category
        );
    }

    /*
    =====================================================
    API: Offset Pagination (LIMIT & OFFSET)

    WHAT THIS API DOES:
    - Fetches products using limit and offset
    - Supports page-based navigation (page 1, page 2, etc.)

    HOW IT WORKS:
    - limit → number of records to fetch
    - offset → number of records to skip

    PERFORMANCE:
    - Simple and easy to use pagination using LIMIT + OFFSET
    - Works well for small datasets
    - Becomes slow for large datasets because DB scans skipped rows

    WHEN TO USE:
    - Small to medium data size
    - Basic pagination requirements

    WHEN TO AVOID:
    - Large tables (performance degrades with high offset values)
    - High-performance APIs

    NOTE:
    - Prefer keyset pagination for better performance on large data
    =====================================================
    */
    @GetMapping("/page")
    public Flux<ProductResponse> getProducts(
            @RequestParam int limit,
            @RequestParam int offset) {

        return loggingUtil.logFlux(
                productService.getProducts(limit, offset),
                "Get Products (Offset Pagination) | limit=" + limit + ", offset=" + offset
        );
    }

    /*
    =====================================================
    CONCEPT DEMONSTRATED: Keyset Pagination

    API: Get Products (Keyset Pagination)

     WHAT THIS API DOES:
    - Fetches next set of products based on last seen record
    - Uses createdAt as cursor instead of offset

    HOW IT WORKS:
    - lastCreatedAt → last record from previous response
    - limit → number of records to fetch
    - Fetches records with createdAt < lastCreatedAt

    PERFORMANCE:
    - Much faster than OFFSET pagination
    - Uses index (created_at) efficiently
    - Avoids scanning skipped rows

    NOTE:
    - Recommended approach for scalable systems
    =====================================================
    */
    @GetMapping("/next-page")
    public Flux<ProductResponse> getNextPage(
            @RequestParam LocalDateTime lastCreatedAt,
            @RequestParam int limit) {

        return loggingUtil.logFlux(
                productService.getNextPage(lastCreatedAt, limit),
                "Get Products (Keyset Pagination) | lastCreatedAt=" + lastCreatedAt + ", limit=" + limit
        );
    }

    /*
    =====================================================
    CONCEPT DEMONSTRATED: Parallel Reactive Aggregation

    API: Get Product Summary (Product + Orders + Revenue)

    CORE IDEA:
    - Execute independent operations in parallel
    - Combine results using Mono.zip

    WHAT THIS API DOES:
    - Fetches product details
    - Gets total number of orders
    - Calculates total revenue
    - Returns combined response

    PERFORMANCE:
    - Uses Mono.zip for parallel, non-blocking execution
    - Reduces overall response time
    - Efficient resource utilization (event-loop model)

    IMPORTANT NOTES:
    - No CompletableFuture required (reactive streams handle parallelism)
    - No Executor or manual thread management needed
    - Avoids blocking and context-switching overhead
    - Cleaner and more maintainable reactive code
    =====================================================
    */
    @GetMapping("/{id}/summary")
    public Mono<ProductSummaryResponse> getProductSummary(@PathVariable Long id) {
        return loggingUtil.logMono(
                productService.getProductSummary(id),
                "Get Product Summary | id=" + id
        );
    }

    /*
    =====================================================
    CONCEPT DEMONSTRATED: Bulk API

    API: Create Products in Bulk

    PERFORMANCE:
    - Reduces multiple HTTP calls
    - Batch DB operations → fewer round trips
    - Improves write throughput
    =====================================================
    */
    @PostMapping("/bulk")
    public Flux<ProductResponse> createProductsBulk(
            @Valid @RequestBody BulkProductRequest request) {

        return loggingUtil.logFlux(
                productService.createProductsBulk(request),
                "Bulk Create Products | count=" + request.products().size()
        );
    }

    /*
    =====================================================
    CONCEPT DEMONSTRATED: Projection Queries

    API: Get Product Summaries

    PERFORMANCE:
    - Fetches only required fields (name, price)
    - Avoids SELECT *
    - Reduces payload size and DB load
    - Faster response for listing APIs
    =====================================================
    */
    @GetMapping("/summary-lite")
    public Flux<ProductSummaryLiteResponse> getProductSummaries() {
        return productService.getProductSummaries();
    }
}