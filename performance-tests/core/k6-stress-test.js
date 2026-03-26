/*
=====================================================
k6 STRESS TEST (High Load / Breaking Point)

WHAT IS k6:
- k6 is a performance testing tool
- Used to simulate users and measure API performance

SETUP k6:
- Mac: brew install k6
- Windows: choco install k6

HOW TO RUN:
k6 run performance-tests/core/k6-stress-test.js

PURPOSE:
- Push system beyond normal capacity
- Identify breaking point (slow response, failures)
- Measure how system behaves under heavy load

WHAT WE ARE TESTING:
- Core APIs under high traffic

STAGES EXPLAINED:
- duration: how long this stage runs
- target: number of virtual users (VUs)

Example:
{ duration: '20s', target: 200 }
→ Increase load to 200 users over 20 seconds
=====================================================
*/

import http from 'k6/http';

export const options = {
    stages: [
        { duration: '10s', target: 50 },   // normal load
        { duration: '20s', target: 100 },  // increased load
        { duration: '20s', target: 200 },  // stress level
        { duration: '10s', target: 0 },    // ramp-down
    ],
};

const BASE_URL = 'http://localhost:8080/api/v1/products';

export default function () {

    http.get(`${BASE_URL}/1`);
    http.get(`${BASE_URL}/1/summary`);
}