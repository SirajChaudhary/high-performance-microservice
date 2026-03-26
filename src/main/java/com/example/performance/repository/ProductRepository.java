package com.example.performance.repository;

import com.example.performance.dto.ProductSummaryLiteResponse;
import com.example.performance.entity.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/*
=====================================================
REPOSITORY: ProductRepository

PERFORMANCE (R2DBC vs JPA ORM):

- Uses reactive R2DBC (non-blocking DB calls)
- Does not use EntityManager or Hibernate session
- No lazy loading, proxies, or dirty checking overhead
- Lightweight direct SQL mapping (no ORM layer)

- Event-loop model handles concurrent requests efficiently
- Better CPU utilization (no thread blocking)
- Scales well under high load

- Lower memory usage compared to JPA entities
- Faster response time for I/O-heavy operations

RESULT:
- Higher throughput
- Better scalability
- Reduced resource consumption
=====================================================
*/
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    /*
    =====================================================
    SEARCH BY NAME

    PERFORMANCE:
    - DB-level filtering using ILIKE (case-insensitive)
    - Index on name improves search performance
    =====================================================
    */
    @Query("SELECT * FROM products WHERE name ILIKE '%' || :name || '%'")
    Flux<Product> searchByName(String name);

    /*
    =====================================================
    FILTER BY CATEGORY

    PERFORMANCE:
    - DB-level filtering
    - Index on category improves lookup speed
    =====================================================
    */
    @Query("SELECT * FROM products WHERE category = :category")
    Flux<Product> findByCategory(String category);

    /*
    =====================================================
    SORT BY PRICE

    PERFORMANCE:
    - Sorting handled at DB level
    - Index on price improves sorting efficiency
    =====================================================
    */
    @Query("SELECT * FROM products ORDER BY price ASC")
    Flux<Product> findAllOrderByPriceAsc();

    /*
    =====================================================
    OFFSET PAGINATION

    PERFORMANCE:
    - Uses LIMIT + OFFSET
    - Slower for large datasets (DB scans skipped rows)
    =====================================================
    */
    @Query("SELECT * FROM products LIMIT :limit OFFSET :offset")
    Flux<Product> findAllWithPagination(int limit, int offset);

    /*
    =====================================================
    KEYSET PAGINATION

    PERFORMANCE:
    - Uses indexed column (created_at)
    - Avoids scanning skipped rows
    - Faster than OFFSET for large datasets
    =====================================================
    */
    @Query("SELECT * FROM products WHERE created_at < :lastCreatedAt ORDER BY created_at DESC LIMIT :limit")
    Flux<Product> findNextPage(java.time.LocalDateTime lastCreatedAt, int limit);

    /*
    =====================================================
    FILTER + SORT

    PERFORMANCE:
    - Combines filtering and sorting in DB
    - Index on (category, price) improves performance
    =====================================================
    */
    @Query("SELECT * FROM products WHERE category = :category ORDER BY price ASC")
    Flux<Product> findByCategoryOrderByPrice(String category);

    /*
    =====================================================
    PROJECTION QUERY

    PERFORMANCE:
    - Fetches only required columns (name, price)
    - Reduces DB load and data transfer
    - Improves response time for lightweight APIs
    =====================================================
    */
    @Query("SELECT name, price FROM products")
    Flux<ProductSummaryLiteResponse> findAllProductSummaries();
}