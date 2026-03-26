/*
=====================================================
k6 WRITE TEST (POST API Performance)

WHAT IS k6:
- k6 is a performance testing tool
- Used to simulate users and measure API performance

SETUP k6:
- Mac: brew install k6
- Windows: choco install k6

HOW TO RUN:
k6 run performance-tests/advanced/k6-write-test.js

PURPOSE:
- Test performance of write operations (POST API)
- Measure database insert performance under load

WHAT WE ARE TESTING:
- Create Product API

CONFIG EXPLAINED:
- vus: number of users
- duration: test duration

Example:
vus: 20, duration: '20s'
→ 20 users continuously creating products

NOTE:
- Write operations are heavier than read operations
- Helps identify DB bottlenecks and write latency
=====================================================
*/

import http from 'k6/http';

export const options = {
    vus: 20,
    duration: '20s',
};

const BASE_URL = 'http://localhost:8080/api/v1/products';

export default function () {

    const payload = JSON.stringify({
        name: "Test Product",
        category: "Electronics",
        price: 1000,
        stock: 10
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    http.post(`${BASE_URL}`, payload, params);
}