# Performance Tests (k6)

## WHAT IS k6
- k6 is a performance testing tool
- Used to simulate users and measure API performance

---

## SETUP k6
- Mac: `brew install k6`
- Windows: `choco install k6`

---

## PERFORMANCE TESTS FOLDER STRUCTURE

- `core/` → basic performance tests (load, stress, spike, soak)
- `advanced/` → optimization and comparison tests (cache, pagination, etc.)

---

## HOW TO RUN

Run any test using:

```bash
k6 run performance-tests/<folder-name>/<file-name>.js
```

Examples:

```bash
k6 run performance-tests/core/k6-load-test.js
k6 run performance-tests/advanced/k6-cache-test.js
```

---

## TEST TYPES

### Core Tests

- **Load Test**  
  Simulates normal traffic to measure response time and stability under expected load.

- **Stress Test**  
  Gradually increases load to identify the system's breaking point.

- **Spike Test**  
  Simulates sudden traffic spikes to test system behavior and recovery.

- **Soak Test**  
  Runs the system for a long duration to detect memory leaks and performance degradation.

---

### Advanced Tests

- **Baseline Test**  
  Measures API performance without load (used as a reference for comparison).

- **Cache Test**  
  Validates caching effectiveness (DB hit vs cache hit performance).

- **Pagination Test**  
  Compares OFFSET vs Keyset pagination performance.

- **Write Test**  
  Tests performance of write operations (POST API, DB inserts).

---

### Extending Performance Tests

We can add more advanced performance test scripts based on requirements.

#### Examples:

- **Concurrency Test**  
  Tests behavior under very high concurrent users.

- **End-to-End Flow Test**  
  Simulates a real user journey (create → search → fetch → summary).

- **Mixed Workload Test**  
  Combines read and write operations together.

- **Cache Warmup Test**  
  Measures performance before and after cache warm-up.

- **Database Stress Test**  
  Focuses on DB-heavy endpoints.

---

## PURPOSE OF THESE TESTS

- Validate high-performance microservice behavior
- Measure latency, throughput, and system stability
- Demonstrate the impact of:
  - Caching
  - Pagination strategies
  - Reactive programming
- Identify bottlenecks under load

---

## NOTE

- Ensure the application is running (`localhost:8080`) before executing tests
- Use realistic data for accurate performance insights
- Compare results across tests to demonstrate optimizations
