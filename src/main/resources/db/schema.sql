-- =====================================================
-- Create database manually before running this script
-- E.g. CREATE DATABASE performance_db;
-- =====================================================

-- =====================================================
-- Tables + Indexes (High Performance Optimized)
-- =====================================================

-- =====================================================
-- CLEANUP (Drop if exists)
-- =====================================================

-- Drop child table first (FK dependency)
DROP TABLE IF EXISTS orders CASCADE;

-- Drop parent table
DROP TABLE IF EXISTS products CASCADE;


-- =====================================================
-- PRODUCTS TABLE
-- =====================================================
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(255) NOT NULL,

    category VARCHAR(100) NOT NULL,

    price NUMERIC(10,2) NOT NULL,

    stock INT NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- =====================================================
-- ORDERS TABLE
-- =====================================================
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,

    product_id BIGINT NOT NULL,

    quantity INT NOT NULL,

    total_price NUMERIC(10,2) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_product
        FOREIGN KEY(product_id)
        REFERENCES products(id)
);


-- =====================================================
-- INDEXES (CRITICAL FOR PERFORMANCE)
-- =====================================================

-- PRODUCTS INDEXES

-- Used for search queries
-- (index recommended to improve filtering performance)
CREATE INDEX idx_products_name ON products(name);

-- Used for filtering
CREATE INDEX idx_products_category ON products(category);

-- Used for sorting & range queries
CREATE INDEX idx_products_price ON products(price);

-- Used for pagination (keyset)
CREATE INDEX idx_products_created_at ON products(created_at);

-- Composite index (filter + sort)
CREATE INDEX idx_products_category_price ON products(category, price);


-- ORDERS INDEXES

-- Used for joins and filtering
CREATE INDEX idx_orders_product_id ON orders(product_id);

-- Used for pagination
CREATE INDEX idx_orders_created_at ON orders(created_at);

-- Composite index (filter + sort)
CREATE INDEX idx_orders_product_created ON orders(product_id, created_at);

-- Used for aggregation queries
CREATE INDEX idx_orders_total_price ON orders(total_price);