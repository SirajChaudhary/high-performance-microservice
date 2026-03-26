# k6 + Prometheus + Grafana Integration Guide

## Overview

This guide explains how to integrate **k6**, **Prometheus**, and **Grafana** to visualize performance metrics for your microservice.

You will be able to monitor:
- API response time
- Throughput (requests/sec)
- Error rate
- CPU and memory usage
- JVM metrics

---

## Step 1 — Add Spring Boot Metrics

### Add Dependency

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### Configure `application.yml`

```yaml
management:
  endpoints:
    web:
      exposure:
        include: prometheus
  endpoint:
    prometheus:
      enabled: true
```

### Verify

Open:
```
http://localhost:8080/actuator/prometheus
```

---

## Step 2 — Configure Prometheus

Create `prometheus.yml`:

```yaml
global:
  scrape_interval: 5s

scrape_configs:
  - job_name: 'spring-boot'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8080']

  - job_name: 'k6'
    static_configs:
      - targets: ['host.docker.internal:6565']
```

---

## Step 3 — Run Prometheus and Grafana

Create `docker-compose.yml`:

```yaml
version: '3'

services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
```

Run:

```bash
docker-compose up -d
```

---

## Step 4 — Run k6 with Prometheus Output

```bash
k6 run --out experimental-prometheus-rw performance-tests/core/k6-load-test.js
```

---

## Step 5 — Access Grafana

- URL: http://localhost:3000  
- Username: admin  
- Password: admin  

---

## Step 6 — Add Prometheus Data Source

- Go to **Settings → Data Sources**
- Add Prometheus:
  - URL: `http://prometheus:9090`
- Click **Save & Test**

---

## Step 7 — Import Dashboards

### k6 Dashboard
- Dashboard ID: `2587`

### Spring Boot Dashboard (Optional)
- Dashboard ID: `4701`

---

## Step 8 — What You Will See

### k6 Metrics
- Request rate (throughput)
- Response time (avg, p95)
- Error rate

### Spring Boot Metrics
- CPU usage
- Memory usage
- JVM threads
- API latency

---

## Final Flow

```
Spring Boot → Prometheus → Grafana
k6 → Prometheus → Grafana
```

---

## Notes

- Ensure application is running on `localhost:8080`
- Use realistic data for better insights
- On Linux, replace `host.docker.internal` with `localhost`
