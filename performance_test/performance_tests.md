# Performance Test Screenshots

This document contains the results of performance tests executed using **k6**. Each scenario simulates real-world API usage under varying loads, showcasing system behavior across varying Virtual User (VU) counts and durations.

---

## Script: `register_drivers.js`

### 🔸 1000 VUs | Duration: 1m
![register_drivers_1000VU.png](screenshots/register_drivers_1000VU.png)


### 🔸 5000 VUs | Duration: 1m
![register_drivers_5000VU.png](screenshots/register_drivers_5000VU.png)


### 🔸 10000 VUs | Duration: 2m
![register_drivers_10000VU.png](screenshots/register_drivers_10000VU.png)

---

## Script: `test_ride_flow.js`

### 🔸 1000 VUs | Duration: 1m
![test_ride_flow_1000VU.png](screenshots/test_ride_flow_1000VU.png)


### 🔸 5000 VUs | Duration: 1m30s
![test_ride_flow_5000VU.png](screenshots/test_ride_flow_5000VU.png)


### 🔸 10000 VUs | Duration: 2m
![test_ride_flow_10000VU.png](screenshots/test_ride_flow_10000VU.png)

---

## Script: `test_admin_usage.js`

### 🔸 100 VUs | Duration: 30s
![test_admin_usage_100VU.png](screenshots/test_admin_usage_100VU.png)


### 🔸 500 VUs | Duration: 30s
![test_admin_usage_500VU.png](screenshots/test_admin_usage_500VU.png)


### 🔸 1000 VUs | Duration: 30s
![test_admin_usage_1000VU.png](screenshots/test_admin_usage_1000VU.png)
