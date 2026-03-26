/*
=====================================================
k6 SPIKE TEST (Sudden Traffic Burst)

WHAT IS k6:
- k6 is a performance testing tool
- Used to simulate users and measure API performance

SETUP k6:
- Mac: brew install k6
- Windows: choco install k6

HOW TO RUN:
k6 run performance-tests/core/k6-spike-test.js

PURPOSE:
- Simulates sudden spike in traffic
- Tests how system handles unexpected load
- Checks system recovery after spike

WHAT WE ARE TESTING:
- System stability during sudden traffic increase

STAGES EXPLAINED:
- duration: how long this stage runs
- target: number of virtual users (VUs)

Example:
{ duration: '5s', target: 200 }
→ Sudden jump to 200 users within 5 seconds
=====================================================
*/

import http from 'k6/http';

export const options = {
    stages: [
        { duration: '5s', target: 20 },   // normal load
        { duration: '5s', target: 200 },  // sudden spike
        { duration: '10s', target: 20 },  // recovery
        { duration: '5s', target: 0 },
    ],
};

const BASE_URL = 'http://localhost:8080/api/v1/products';

export default function () {

    http.get(`${BASE_URL}/1`);
    http.get(`${BASE_URL}/search?name=phone`);
}