package com.example.performance.service.impl;

import com.example.performance.cache.CacheKeys;
import com.example.performance.cache.LocalCache;
import com.example.performance.cache.RedisCacheService;
import com.example.performance.dto.*;
import com.example.performance.entity.Product;
import com.example.performance.repository.OrderRepository;
import com.example.performance.repository.ProductRepository;
import com.example.performance.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;

/*
=====================================================
SERVICE: ProductServiceImpl

PURPOSE:
- Implements business logic for product operations
- Coordinates between controller and repository layers

PERFORMANCE:
- Fully reactive (non-blocking using Mono/Flux)
- Uses R2DBC for non-blocking DB access
- Efficient resource utilization (event-loop model)
- Optimized for high concurrency systems
=====================================================
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    private final LocalCache localCache;
    private final RedisCacheService redisCache;

    @Override
    public Mono<ProductResponse> createProduct(ProductRequest request) {

        /*
        =====================================================
        OBJECT CREATION

        PERFORMANCE:
        - Builder pattern avoids multiple setter calls
        - Clean and efficient object construction
        =====================================================
        */
        Product product = Product.builder()
                .name(request.name())
                .category(request.category())
                .price(request.price())
                .stock(request.stock())
                .createdAt(LocalDateTime.now())
                .build();

        /*
        =====================================================
        DATABASE OPERATION

        PERFORMANCE:
        - Non-blocking DB call using R2DBC
        =====================================================
        */
        return productRepository.save(product)
                .map(this::mapToResponse);
    }

    /*
    =====================================================
    GET PRODUCT BY ID

    PURPOSE:
    - Fetch product details by ID
    - Uses multi-level caching (L1 + L2)

    PERFORMANCE:
    - L1 (Caffeine) for ultra-fast access
    - L2 (Redis) for distributed caching
    - Reduces database calls and latency
    =====================================================
    */
    @Override
    public Mono<ProductResponse> getProductById(Long id) {

        String key = CacheKeys.productById(id);

        // L1 Cache (Caffeine): fastest access
        ProductResponse l1Data = localCache.get(key);
        if (l1Data != null) {
            log.info("L1 Cache HIT for key={}", key);
            return Mono.just(l1Data);
        }

        // L2 Cache (Redis): shared cache across instances
        return redisCache.get(key, ProductResponse.class)

                // Populate L1 on L2 hit
                .doOnNext(data -> {
                    log.info("L2 Cache HIT for key={}", key);
                    localCache.put(key, data);
                })

                // Cache miss → fetch from DB
                .switchIfEmpty(
                        Mono.defer(() -> {
                            log.info("CACHE MISS for key={}", key);

                            return productRepository.findById(id)
                                    .switchIfEmpty(Mono.error(new RuntimeException("Product not found")))
                                    .map(this::mapToResponse)

                                    // Store in both L2 and L1 caches
                                    .flatMap(product ->
                                            redisCache.put(key, product)
                                                    .doOnSuccess(v -> localCache.put(key, product))
                                                    .thenReturn(product)
                                    );
                        })
                );
    }

    /*
    =====================================================
    GET ALL PRODUCTS

    PERFORMANCE:
    - Streams data using Flux
    - Efficient for large datasets
    =====================================================
    */
    @Override
    public Flux<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .map(this::mapToResponse);
    }

    /*
    =====================================================
    SEARCH PRODUCTS

    PERFORMANCE:
    - Delegates filtering to DB
    - Avoids in-memory filtering
    =====================================================
    */
    @Override
    public Flux<ProductResponse> searchProducts(String name) {
        return productRepository.searchByName(name)
                .map(this::mapToResponse);
    }

    /*
    =====================================================
    FILTER BY CATEGORY

    PERFORMANCE:
    - DB-level filtering improves efficiency
    =====================================================
    */
    @Override
    public Flux<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategory(category)
                .map(this::mapToResponse);
    }

    /*
    =====================================================
    OFFSET PAGINATION

    PERFORMANCE:
    - Uses LIMIT/OFFSET
    - Simpler but slower for large datasets
    =====================================================
    */
    @Override
    public Flux<ProductResponse> getProducts(int limit, int offset) {
        return productRepository.findAllWithPagination(limit, offset)
                .map(this::mapToResponse);
    }

    /*
    =====================================================
    KEYSET PAGINATION

    PERFORMANCE:
    - Uses indexed cursor (createdAt)
    - Avoids scanning skipped rows
    - Faster for large datasets
    =====================================================
    */
    @Override
    public Flux<ProductResponse> getNextPage(LocalDateTime lastCreatedAt, int limit) {
        return productRepository.findNextPage(lastCreatedAt, limit)
                .map(this::mapToResponse);
    }

    /*
    =====================================================
    PRODUCT SUMMARY (AGGREGATION)

    PERFORMANCE:
    - Uses Mono.zip for parallel execution
    - Fully non-blocking aggregation
    - Reduces overall response time

    IMPORTANT NOTE:
    - CompletableFuture is NOT required here
      (Reactive streams already support parallel execution using Mono.zip)
    - Executor thread is NOT required
      (WebFlux uses event-loop model and manages threads efficiently)
    - Avoids manual thread management and context switching overhead
    - Cleaner, simpler, and more maintainable code
    - Best approach for reactive systems (WebFlux + R2DBC)
    - Recommended over CompletableFuture + Executor in this context
    =====================================================
    */
    @Override
    public Mono<ProductSummaryResponse> getProductSummary(Long productId) {

        // Fetch product (non-blocking)
        Mono<Product> productMono =
                productRepository.findById(productId);

        // Fetch total orders (non-blocking aggregation)
        Mono<Long> totalOrdersMono =
                orderRepository.findByProductId(productId)
                        .count();

        // Calculate total revenue (non-blocking aggregation)
        Mono<BigDecimal> revenueMono =
                orderRepository.findByProductId(productId)
                        .map(o -> o.getTotalPrice())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Combine all results in parallel (non-blocking)
        return Mono.zip(productMono, totalOrdersMono, revenueMono)

                // DEBUG: Track execution start of complex aggregation
                .doOnSubscribe(sub ->
                        log.debug("Fetching product summary for productId={}", productId))

                // ERROR: Capture failures in aggregation pipeline
                .doOnError(error ->
                        log.error("Error while fetching summary for productId={}", productId, error))

                .map(tuple -> {

                    Product product = tuple.getT1();
                    Long totalOrders = tuple.getT2();
                    BigDecimal revenue = tuple.getT3();

                    return new ProductSummaryResponse(
                            product.getId(),
                            product.getName(),
                            product.getCategory(),
                            product.getPrice(),
                            totalOrders,
                            revenue
                    );
                });
    }

    /*
    =====================================================
    BULK INSERT

    PERFORMANCE:
    - Batch insert using saveAll
    - Reduces DB round trips
    - Improves write throughput
    =====================================================
    */
    @Override
    public Flux<ProductResponse> createProductsBulk(BulkProductRequest request) {

        if (request.products() == null || request.products().isEmpty()) {
            log.warn("Bulk insert failed: empty product list");
            return Flux.error(new IllegalArgumentException("Products list cannot be empty"));
        }

        log.info("Bulk insert started for {} products", request.products().size());

        return productRepository.saveAll(
                        Flux.fromIterable(request.products())
                                .map(this::mapToEntity)
                )
                .doOnComplete(() ->
                        log.info("Bulk insert completed for {} products", request.products().size()))

                .doOnError(error ->
                        log.error("Bulk insert failed", error))

                .map(this::mapToResponse);
    }

    /*
    =====================================================
    PROJECTION FETCH

    PERFORMANCE:
    - Fetches only required fields (name, price)
    - Reduces DB load and memory usage
    =====================================================
    */
    public Flux<ProductSummaryLiteResponse> getProductSummaries() {
        return productRepository.findAllProductSummaries();
    }

    /*
    =====================================================
    MAPPER: Request to Entity

    PERFORMANCE:
    - Lightweight manual mapping
    - Faster than reflection-based mappers
    =====================================================
    */
    private Product mapToEntity(ProductRequest request) {
        return Product.builder()
                .name(request.name())
                .category(request.category())
                .price(request.price())
                .stock(request.stock())
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }

    /*
    =====================================================
    MAPPER: Entity to Response

    PERFORMANCE:
    - Direct mapping (no heavy libraries)
    - Improves serialization performance
    =====================================================
    */
    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStock(),
                product.getCreatedAt()
        );
    }
}