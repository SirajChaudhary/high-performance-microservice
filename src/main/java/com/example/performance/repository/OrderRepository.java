package com.example.performance.repository;

import com.example.performance.entity.Order;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/*
=====================================================
REPOSITORY: OrderRepository

PERFORMANCE (R2DBC vs JPA ORM):

- Uses reactive R2DBC (non-blocking DB calls)
- Does not use EntityManager or Hibernate session
- No lazy loading, proxies, or dirty checking overhead
- Lightweight direct SQL mapping (no ORM layer)

- Event-loop model handles multiple requests efficiently
- Better CPU utilization (no thread blocking)
- Scales better under high concurrency

- Lower memory usage compared to JPA entities
- Faster response time for I/O-heavy operations

RESULT:
- Higher throughput
- Better scalability
- Reduced resource consumption
=====================================================
*/
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    /*
    =====================================================
    FIND ORDERS BY PRODUCT

    PERFORMANCE:
    - Filters at DB level (no in-memory filtering)
    - Index on product_id improves lookup speed
    =====================================================
    */
    @Query("SELECT * FROM orders WHERE product_id = :productId")
    Flux<Order> findByProductId(Long productId);

    /*
    =====================================================
    OFFSET PAGINATION

    PERFORMANCE:
    - Uses LIMIT + OFFSET
    - Slower for large offsets (DB scans skipped rows)
    =====================================================
    */
    @Query("SELECT * FROM orders ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    Flux<Order> findAllWithPagination(int limit, int offset);

    /*
    =====================================================
    KEYSET PAGINATION

    PERFORMANCE:
    - Uses indexed column (created_at)
    - Avoids scanning skipped rows
    - Faster than OFFSET for large datasets
    =====================================================
    */
    @Query("SELECT * FROM orders WHERE created_at < :lastCreatedAt ORDER BY created_at DESC LIMIT :limit")
    Flux<Order> findNextPage(java.time.LocalDateTime lastCreatedAt, int limit);

    /*
    =====================================================
    JOIN: ORDER + PRODUCT

    PERFORMANCE:
    - Fetches related data in single query
    - Avoids multiple DB calls (N+1 problem)
    - Index on product_id improves join performance
    =====================================================
    */
    @Query("""
           SELECT o.id, o.product_id, o.quantity, o.total_price, o.created_at,
                  p.name, p.category, p.price
           FROM orders o
           JOIN products p ON o.product_id = p.id
           WHERE o.product_id = :productId
           """)
    Flux<Object[]> findOrdersWithProductDetails(Long productId);

    /*
    =====================================================
    AGGREGATION: TOTAL REVENUE

    PERFORMANCE:
    - Uses DB aggregation (SUM)
    - Avoids in-memory computation
    - Index on product_id improves grouping
    =====================================================
    */
    @Query("""
           SELECT product_id, SUM(total_price) AS total_revenue
           FROM orders
           GROUP BY product_id
           """)
    Flux<Object[]> getTotalRevenuePerProduct();

    /*
    =====================================================
    AGGREGATION: TOTAL QUANTITY

    PERFORMANCE:
    - Uses DB-level aggregation
    - Reduces application-side processing
    =====================================================
    */
    @Query("""
           SELECT product_id, SUM(quantity) AS total_quantity
           FROM orders
           GROUP BY product_id
           """)
    Flux<Object[]> getTotalQuantityPerProduct();

    /*
    =====================================================
    FILTER + SORT

    PERFORMANCE:
    - DB-level filtering and sorting
    - Index on (product_id, created_at) improves performance
    =====================================================
    */
    @Query("""
           SELECT * FROM orders
           WHERE product_id = :productId
           ORDER BY created_at DESC
           """)
    Flux<Order> findByProductIdOrderByCreatedAt(Long productId);
}