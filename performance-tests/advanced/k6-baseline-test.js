/*
=====================================================
k6 BASELINE TEST (No Load / Single User)

WHAT IS k6:
- k6 is a performance testing tool
- Used to simulate users and measure API performance

SETUP k6:
- Mac: brew install k6
- Windows: choco install k6

HOW TO RUN:
k6 run performance-tests/advanced/k6-baseline-test.js

PURPOSE:
- Measure API response time without load
- Acts as baseline for comparison with other tests

WHAT WE ARE TESTING:
- Basic API performance (no concurrency)

CONFIG EXPLAINED:
- vus: number of virtual users (1 user)
- iterations: total requests

Example:
vus: 1, iterations: 10
→ Single user sends 10 requests
=====================================================
*/

import http from 'k6/http';

export const options = {
    vus: 1,
    iterations: 10,
};

const BASE_URL = 'http://localhost:8080/api/v1/products';

export default function () {
    http.get(`${BASE_URL}/1`);
}