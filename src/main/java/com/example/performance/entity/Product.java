package com.example.performance.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
=====================================================
ENTITY: Product

PERFORMANCE (R2DBC vs Traditional JPA ORM):

- Uses reactive R2DBC (non-blocking DB access)
- Does not use EntityManager or Hibernate session
- No lazy loading or proxy objects
- No dirty checking overhead
- Lightweight mapping (direct row-to-object mapping)
- Lower memory usage compared to JPA entities
- Better suited for high-throughput reactive systems

RESULT:
- Faster execution
- Better scalability under load
- Reduced resource consumption
=====================================================
*/

/*
=====================================================
@Data

- Lombok annotation
- Generates getters, setters, toString, equals, and hashCode
- Reduces boilerplate code
- Improves developer productivity
=====================================================
*/
@Data

/*
=====================================================
@Builder

- Lombok annotation
- Implements Builder Design Pattern
- Clean and readable object creation (no multiple setters)
- Reduces boilerplate code
- Improves maintainability for objects with many fields
- Used in service layer to construct objects easily
  Example: Product.builder().name(...).price(...).build()
=====================================================
*/
@Builder

// Maps directly to DB table (lightweight, no heavy ORM layer)
@Table("products")
public class Product {

    // Primary key (automatically indexed for fast lookups)
    @Id
    private Long id;

    // Index recommended to improve search queries
    private String name;

    // Index recommended to improve filtering performance
    private String category;

    // Index recommended to improve sorting and range queries
    private BigDecimal price;

    // Frequently updated field (avoid caching to prevent stale data)
    private Integer stock;

    // Index recommended to improve pagination and sorting
    private LocalDateTime createdAt;
}