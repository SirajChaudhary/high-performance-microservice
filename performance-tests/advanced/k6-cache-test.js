/*
=====================================================
k6 CACHE TEST

WHAT IS k6:
- k6 is a performance testing tool
- Used to simulate users and measure API performance

SETUP k6:
- Mac: brew install k6
- Windows: choco install k6

HOW TO RUN:
k6 run performance-tests/advanced/k6-cache-test.js

PURPOSE:
- Validate caching performance (L1 + L2 cache)
- Compare first request (DB hit) vs repeated requests (cache hit)

WHAT WE ARE TESTING:
- Cached API (Get Product by ID)

CONFIG EXPLAINED:
- vus: number of virtual users
- duration: how long test runs

Example:
vus: 20, duration: '20s'
→ 20 users repeatedly hitting same API (cache utilization)
=====================================================
*/

import http from 'k6/http';

export const options = {
    vus: 20,
    duration: '20s',
};

const BASE_URL = 'http://localhost:8080/api/v1/products';

export default function () {
    http.get(`${BASE_URL}/1`); // same request → should hit cache
}