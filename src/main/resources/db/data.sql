-- =====================================================
-- SAMPLE DATA (FOR TESTING & PERFORMANCE DEMO)
-- =====================================================

-- =========================
-- PRODUCTS DATA
-- =========================
INSERT INTO products (name, category, price, stock, created_at) VALUES
('iPhone 15', 'Electronics', 80000.00, 50, NOW()),
('Samsung Galaxy S23', 'Electronics', 75000.00, 40, NOW()),
('MacBook Pro', 'Electronics', 150000.00, 20, NOW()),
('Dell Laptop', 'Electronics', 90000.00, 30, NOW()),
('Sony Headphones', 'Accessories', 15000.00, 100, NOW()),
('Apple Watch', 'Accessories', 40000.00, 60, NOW()),
('Office Chair', 'Furniture', 10000.00, 80, NOW()),
('Gaming Chair', 'Furniture', 20000.00, 40, NOW());


-- =========================
-- ORDERS DATA
-- =========================
INSERT INTO orders (product_id, quantity, total_price, created_at) VALUES
(1, 2, 160000.00, NOW()),
(2, 1, 75000.00, NOW()),
(3, 1, 150000.00, NOW()),
(1, 3, 240000.00, NOW()),
(5, 2, 30000.00, NOW()),
(6, 1, 40000.00, NOW()),
(7, 4, 40000.00, NOW()),
(8, 2, 40000.00, NOW());