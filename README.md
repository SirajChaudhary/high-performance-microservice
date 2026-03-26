# High Performance Microservice

## Overview

This is a **Reactive High Performance Microservice**

The main goal of this microservice is to **demonstrate how to design and
implement a high-performance, scalable, and non-blocking system** using
modern backend technologies.

------------------------------------------------------------------------

## Why High Performance Microservices?

In modern systems, applications must handle: 
- High concurrency (thousands of users) 
- Low latency requirements 
- Efficient resource utilization 
- Scalability under unpredictable load

Traditional blocking architectures (JPA, thread-per-request) struggle
under heavy load.

This microservice demonstrates how to: 
- Reduce thread usage 
- Avoid blocking operations 
- Improve throughput 
- Scale efficiently with fewer resources

------------------------------------------------------------------------

## What This Project Demonstrates

This microservice is built to **showcase real-world performance
improvements** using various techniques:

-   Reactive non-blocking APIs using WebFlux
-   Multi-level caching (L1: Caffeine, L2: Redis)
-   Database optimization using R2DBC
-   Efficient pagination (Offset vs Keyset)
-   Bulk operations for better throughput
-   Projection queries to reduce payload size
-   Reactive parallel processing using Mono.zip
-   Backpressure handling for large data streams
-   Async logging to avoid blocking
-   Correlation ID for distributed tracing
-   Cache warm-up to eliminate cold start latency
-   Load testing using k6

------------------------------------------------------------------------

## Microservice Architecture (Performance Focus)

```
Client
│ 
▼ 
Controller Layer (Non-blocking APIs) 
│ 
▼ 
Service Layer (Reactive Business Logic) 
│ 
▼ Cache Layer 
├─ L1 Cache (Caffeine - in-memory) 
└─ L2 Cache (Redis - distributed) 
│ 
▼ Repository Layer (R2DBC - non-blocking DB) 
│ 
▼ Database (PostgreSQL with indexes)
```

Controller → Service → Repository → Database

-   Fully reactive end-to-end
-   No blocking calls
-   Event-loop based execution (Netty)

------------------------------------------------------------------------

## Technology Stack

-   Java 21
-   Spring Boot 3.x
-   Spring WebFlux
-   Project Reactor (Mono, Flux)
-   R2DBC (Reactive DB access)
-   PostgreSQL
-   Redis (Reactive)
-   Caffeine Cache
-   Maven
-   k6 (Performance Testing)

### Technologies We Avoided (For Performance)

We intentionally did NOT use:

-   JPA / Hibernate → Blocking ORM, adds overhead
-   Spring Cache (@Cacheable) → Blocking, not reactive-friendly
-   CompletableFuture → Not needed in reactive systems
-   Executor Threads → WebFlux handles concurrency via event-loop

------------------------------------------------------------------------

## Key Performance Features

### 1. Reactive Programming (WebFlux + Reactor)
- Non-blocking execution (no thread waiting)
- Event-loop model (Netty) → fewer threads 
- Handles high concurrency efficiently 
- Better CPU utilization under load

### 2. Multi-Level Caching (L1 + L2)
- L1: In-memory cache (Caffeine) → ultra-fast access 
- L2: Distributed cache (Redis) → shared across instances 
- Reduces database calls significantly 
- Improves response time for hot data 
- TTL-based eviction prevents stale data

### 3. R2DBC (Reactive Database Access)
- Fully non-blocking database calls
- No JDBC / Hibernate overhead 
- Lightweight row-to-object mapping 
- Efficient connection pooling 
- Better scalability under high concurrency

### 4. Pagination Optimization
- Offset Pagination:
    - Simple to implement 
    - Slower for large datasets (scans skipped rows)
- Keyset Pagination:
  - Uses indexed cursor (created_at)
  - Avoids scanning unnecessary rows
  - Much faster and scalable

### 5. Parallel Processing (Reactive Aggregation)
- Uses Mono.zip(...) for concurrent execution 
- Executes independent operations in parallel 
- No threads or executors required 
- Reduces total response time significantly

### 6. Backpressure Handling
- Controls data flow between producer and consumer 
- Prevents memory overload 
- Ensures system stability under high load 
- Uses .onBackpressureBuffer(...)

### 7. Async Logging
- Uses Logback AsyncAppender 
- Non-blocking logging 
- Prevents request thread delays 
- Improves throughput under heavy load

### 8. Database Query Optimization
- Indexes on:
    - name, category, price, created_at
- Composite indexes for filtering + sorting
- DB-level filtering (avoids in-memory processing)
- Projection queries (fetch only required fields)
- Faster queries and reduced payload size

### 9. Connection & Resource Optimization
- R2DBC connection pooling 
- Pre-initialized connections (reduces first-call latency)
- Idle timeout configuration 
- Lightweight validation query (SELECT 1)
- Efficient resource utilization

### 10. Cache Warm-Up Strategy
- Preloads frequently accessed data at startup
- Populates both L1 (Caffeine) and L2 (Redis)
- Eliminates cold-start latency
- Reduces initial database load

### 11. Bulk Operations
- Batch insert using saveAll 
- Reduces multiple DB round trips 
- Improves write throughput

### 12. Lightweight DTOs (Java Records)
- Immutable data structures
- Reduced memory footprint
- Faster serialization/deserialization
- Ideal for read-heavy APIs

### 13. Compression & Network Optimization
- GZIP compression enabled
- Applied only for responses > 1KB
- Reduces bandwidth usage
- Improves response time over network

### 14. Observability & Monitoring
- Spring Boot Actuator (health, metrics, Prometheus)
- Correlation ID for request tracing
- Helps identify performance bottlenecks quickly

### 15. Graceful Shutdown Handling
- Ensures ongoing requests complete before shutdown 
- Prevents request loss under load 
- Improves system stability

### 16. Reactive Logging & Slow API Detection
- Tracks API execution time 
- Configurable slow API threshold 
- Helps identify performance bottlenecks

### 17. Performance Testing (k6)
- Comprehensive performance testing using k6
- Validates system under multiple scenarios:
    - Baseline Test → single-user performance
    - Load Test → normal traffic
    - Stress Test → high load / breaking point
    - Spike Test → sudden traffic burst
    - Soak Test → long-running stability
    - Cache Test → cache efficiency validation
    - Pagination Test → offset vs keyset comparison
    - Write Test → DB insert performance
- Measures:
    - Response time
    - Throughput
    - Error rates
    - System stability under load

------------------------------------------------------------------------

## Caching Strategy (L1 + L2)

### Flow:
```
Request → L1 Cache → L2 Cache → Database
```

### L1 Cache (Caffeine)

-   In-memory
-   Ultra-fast (nanoseconds)
-   Used for hot data
-   Reduces Redis calls

### L2 Cache (Redis)

-   Distributed cache
-   Shared across instances
-   Reduces database load

### Why NOT Spring Cache?

-   Spring Cache is **blocking**
-   Not suitable for reactive systems (WebFlux)
-   Breaks non-blocking execution

------------------------------------------------------------------------

## Parallel Execution (Reactive)

We use:

```
Mono.zip (productMono, ordersMono, revenueMono)
```

### Why this is important:

-   Executes multiple independent operations **in parallel**
-   No threads manually created
-   No blocking
-   Faster response time

### Example:
- Fetch product
- Fetch order count
- Calculate revenue

All run simultaneously → combined result

------------------------------------------------------------------------

## Database Optimization

### Indexes

Used on: 
- WHERE 
- JOIN 
- ORDER BY

### DB-Level Filtering

```
SELECT \* FROM products WHERE category = ?
```

### Projection Query

```
SELECT name, price FROM products
```

Benefits:
- Smaller payload
- Faster response
- Reduced DB load

------------------------------------------------------------------------

## Pagination

### Offset Pagination
```
LIMIT + OFFSET
```
- Simple
- Slower for large data
### Keyset Pagination
```
WHERE created_at \< ?
```
- Uses index
- Much faster
- Recommended for scalable systems
------------------------------------------------------------------------

## Search & Filter Optimization
- Indexed columns
- DB-level filtering (Avoid in-memory filtering) 

------------------------------------------------------------------------

## Logging Optimization
- Non-blocking logging
- Minimal logs for performance

------------------------------------------------------------------------

## Startup Optimization
- Cache warm-up at startup
- Optional lazy initialization:

```
app.setLazyInitialization(true);
```

------------------------------------------------------------------------

## Connection Optimization
- R2DBC connection pooling
- Idle timeout configured
- Efficient resource usage

------------------------------------------------------------------------

## Cache Warm-Up Strategy
- Preload frequently accessed data
- Populate both L1 and L2 caches
- Eliminates cold-start latency

------------------------------------------------------------------------

## Performance Guidelines

API Layer: 
- avoid large payload 
- use pagination

DB Layer: 
- use indexes 
- avoid SELECT \*

Cache: 
- cache read-heavy data 
- use TTL

Reactive: 
- avoid blocking calls

Logging: 
- async logging

------------------------------------------------------------------------

## APIs
Base URL: http://localhost:8080/api/v1/products

- POST / → Create Product
- GET /{id} → Get Product by ID
- GET / → Get All Products
- GET /search → Search Products
- GET /category → Filter by Category
- GET /page → Offset Pagination
- GET /next-page → Keyset Pagination
- GET /{id}/summary → Aggregated Summary
- POST /bulk → Bulk Create
- GET /summary-lite → Projection API

------------------------------------------------------------------------

## How To Run

**Step1:** Clone the project
```
git clone https://github.com/SirajChaudhary/high-performance-microservice.git
```

**Step2:** Create DB
```
CREATE DATABASE performance_db;
```
Run DB scripts to create tables and sample data
```
resources/db/schema.sql

resources/db/data.sql
```

**Step3:** Start Redis Cache Server
```
docker run -d -p 6379:6379 redis
```

**Step4:** Start application
```
mvn clean install

mvn spring-boot:run
```

**Step5:** Run APIs

-   Import Postman collection
-   Run APIs

**Step6:** Performance Testing

Available Tests
- Baseline Test → Single user performance
- Load Test → Normal traffic
- Stress Test → Breaking point
- Spike Test → Sudden traffic burst
- Soak Test → Long running stability
- Cache Test → Cache performance validation
- Pagination Test → Offset vs Keyset comparison
- Write Test → DB insert performance

Refer
- [How to run Performance Tests using k6](./docs/what-is-k6.md)
- [Integrate k6 + Prometheus + Grafana](./docs/k6-prometheus-grafana-integration.md)

------------------------------------------------------------------------

## Conclusion

This is a production-style high-performance microservice designed to demonstrate:
- Scalability
- Efficiency
- Low latency
- Modern reactive architecture

------------------------------------------------------------------------

## License

Free software, by [Siraj Chaudhary](https://www.linkedin.com/in/sirajchaudhary/)
