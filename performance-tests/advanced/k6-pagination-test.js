/*
=====================================================
k6 PAGINATION TEST (Offset vs Keyset)

WHAT IS k6:
- k6 is a performance testing tool
- Used to simulate users and measure API performance

SETUP k6:
- Mac: brew install k6
- Windows: choco install k6

HOW TO RUN:
k6 run performance-tests/advanced/k6-pagination-test.js

PURPOSE:
- Compare OFFSET pagination vs Keyset pagination
- Demonstrate performance difference for large datasets

WHAT WE ARE TESTING:
- OFFSET API (page?limit & offset) → slower for large data
- Keyset API (next-page) → optimized and faster

CONFIG EXPLAINED:
- vus: number of users
- duration: test duration

Example:
vus: 30, duration: '30s'
→ 30 users testing both pagination APIs continuously
=====================================================
*/

import http from 'k6/http';

export const options = {
    vus: 30,
    duration: '30s',
};

const BASE_URL = 'http://localhost:8080/api/v1/products';

export default function () {

    // OFFSET pagination (less efficient)
    http.get(`${BASE_URL}/page?limit=10&offset=100`);

    // Keyset pagination (high-performance)
    http.get(`${BASE_URL}/next-page?lastCreatedAt=2025-01-01T10:00:00&limit=10`);
}