/*
=====================================================
k6 LOAD TEST (Normal Traffic)

WHAT IS k6:
- k6 is a performance testing tool
- Used to simulate users and measure API performance

SETUP k6:
- Mac: brew install k6
- Windows: choco install k6

HOW TO RUN:
k6 run performance-tests/core/k6-load-test.js

PURPOSE:
- Simulates normal/expected user traffic
- Checks system performance under regular load
- Measures response time and success rate

WHAT WE ARE TESTING:
- Basic APIs (get by id, search, filter, summary)

STAGES EXPLAINED:
- duration: how long this stage runs
- target: number of virtual users (VUs)

Example:
{ duration: '10s', target: 20 }
→ Gradually increase to 20 users over 10 seconds
=====================================================
*/

import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 20 }, // ramp-up users
        { duration: '30s', target: 50 }, // steady load
        { duration: '10s', target: 0 },  // ramp-down
    ],
};

const BASE_URL = 'http://localhost:8080/api/v1/products';

export default function () {

    // Get product (cached API)
    let res = http.get(`${BASE_URL}/1`);
    check(res, { 'status 200': (r) => r.status === 200 });

    // Search API
    http.get(`${BASE_URL}/search?name=phone`);

    // Filter API
    http.get(`${BASE_URL}/category?category=Electronics`);

    // Summary API (parallel processing)
    http.get(`${BASE_URL}/1/summary`);

    sleep(1); // simulate user think time
}