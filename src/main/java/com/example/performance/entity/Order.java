package com.example.performance.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
=====================================================
ENTITY: Order

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
  Example: Order.builder().productId(...).totalPrice(...).build()
=====================================================
*/
@Builder

// Maps directly to DB table (lightweight, no heavy ORM layer)
@Table("orders")
public class Order {

    // Primary key (automatically indexed for fast lookups)
    @Id
    private Long id;

    // Foreign key (index recommended to improve join performance)
    private Long productId;

    private Integer quantity;

    // Index recommended to improve aggregation queries
    private BigDecimal totalPrice;

    // Index recommended to improve sorting and pagination
    private LocalDateTime createdAt;
}