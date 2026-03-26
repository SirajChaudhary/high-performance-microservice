/*
=====================================================
k6 SOAK TEST (Long Running Stability Test)

WHAT IS k6:
- k6 is a performance testing tool
- Used to simulate users and measure API performance

SETUP k6:
- Mac: brew install k6
- Windows: choco install k6

HOW TO RUN:
k6 run performance-tests/core/k6-soak-test.js

PURPOSE:
- Runs system for long duration
- Detects memory leaks and performance degradation
- Ensures system stability over time

WHAT WE ARE TESTING:
- API stability under sustained load

CONFIG EXPLAINED:
- vus: number of constant users
- duration: total test run time

Example:
vus: 50, duration: '5m'
→ 50 users continuously for 5 minutes
=====================================================
*/

import http from 'k6/http';

export const options = {
    vus: 50,           // constant users
    duration: '5m',    // long-running test
};

const BASE_URL = 'http://localhost:8080/api/v1/products';

export default function () {

    http.get(`${BASE_URL}/1`);
    http.get(`${BASE_URL}/category?category=Electronics`);
}