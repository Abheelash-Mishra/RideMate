# RideMate

RideMate is a console-based and RESTAPI-enabled ride-hailing application written in Java using **Spring Boot**. It supports both **CLI commands** and **REST API endpoints** for managing rides, drivers, and riders.

**Deployed Link:** https://ridemate-ikfj.onrender.com

## Features
- **CLI Mode**: Interact with the system through terminal commands.
- **REST API Mode**: Use HTTP requests to manage rides and users.
- **PostgreSQL Database**: Primary database used by the main application.
- **H2 for Testing**: In-memory H2 database used for unit and integration tests.
- **Flexible Payment System**: Supports multiple payment methods via interfaces.
- **Unit & Integration Testing**: Tested using JUnit and MockMvc.

---

## Model Diagram
![model_diagram.png](model_diagram.png)

---
## Installation & Setup
### **1. Clone the Repository**
```sh
git clone https://github.com/Abheelash-Mishra/RideMate.git
cd riderapp
```

### **2. Install Dependencies**
Ensure you have **Maven** installed. Then, run this to generate the package:
```sh
mvn clean install
```

### **3. Running in CLI Mode**
To run it in CLI mode, enter the following command into your terminal.
```sh
java -jar target/riderapp-1.0.jar cli
```

Now, you can run the CLI application. Below is a command sequence example.
```sh
ADD_DRIVER d1@email.com 9876556789 1 1
ADD_DRIVER d1@email.com 9876556789 4 5
ADD_DRIVER d1@email.com 9876556789 2 2
ADD_RIDER r1@email.com 9876556789 0 0
MATCH 1
START_RIDE 2 1 Beach 4 5
STOP_RIDE 1 32
BILL 1
```

### **4. Running as a REST API**
Since RiderApp is a Spring Boot application, it has an embedded Apache Tomcat server. Simply run the application by:
1. Running this command:
   ```sh
   java -jar target/riderapp-1.0.jar
   ```
         
2. Access the API at `http://localhost:8080/`


#### **API Endpoints**
| Method     | Endpoint                  | Description               |
|------------|---------------------------|---------------------------|
| **POST**   | `/driver/add`             | Add a driver              |
| **POST**   | `/driver/rate`            | Rate a driver             |
| **POST**   | `/payment/pay`            | Pay for a ride            |
| **POST**   | `/payment/add-money`      | Add money to wallet       |
| **POST**   | `/ride/rider/add`         | Add a rider               |
| **GET**    | `/ride/match/`            | Match rider with a driver |
| **POST**   | `/ride/start/`            | Start a ride              |
| **POST**   | `/ride/stop/`             | Stop a ride               |
| **GET**    | `/ride/bill/`             | Generate bill for a ride  |
| **GET**    | `/admin/drivers/earnings` | List a driver's earnings  |
| **DELETE** | `/admin/drivers/remove`   | Remove a driver           |
| **GET**    | `/admin/drivers/list`     | List N drivers            |

Test with **Postman** or **cURL**:
```sh
curl -X POST http://localhost:8080/riderapp/ride/rider/add?email=test@gmail.com&phoneNumber=9876556789&x=0&y=0
```

---


# Testing

### **Run Tests**

To run the unit and integration tests for this project, you can use the following command:

```sh
mvn test
```

This will run all tests and show the results in the terminal.

---

## Performance Report

The following table summarizes the API performance tested using [k6](https://k6.io). Metrics were gathered across various virtual user (VU) counts and durations.

| Script Name         | VUs   | Duration | Req/s    | Avg R.D. | Min R.D. | Max R.D.  | p(90) R.D. | p(95) R.D. | p(90) N.L. | p(95) N.L. | Success Rate |
|---------------------|-------|----------|----------|----------|----------|-----------|------------|------------|------------|------------|--------------|
| register_drivers.js | 1000  | 1m       | 16.68/s  | 6.34 ms  | 2.94 ms  | 206.65 ms | 9.85 ms    | 10.46 ms   | 9 ms       | 9 ms       | 100%         |
|                     | 5000  | 1m       | 83.22/s  | 3.66 ms  | 1.51 ms  | 159.90 ms | 3.77 ms    | 6.67 ms    | 3 ms       | 6 ms       | 100%         |
|                     | 10000 | 2m       | 84.12/s  | 3.65 ms  | 1.32 ms  | 214.88 ms | 2.73 ms    | 3.84 ms    | 2 ms       | 3 ms       | 100%         |
| test_ride_flow.js   | 1000  | 1m       | 98.56/s  | 7.35 ms  | 1.41 ms  | 33.40 ms  | 11.89 ms   | 13.44 ms   | 11 ms      | 13 ms      | 100%         |
|                     | 5000  | 1m30s    | 337.62/s | 11.35 ms | 1.34 ms  | 156.17 ms | 19.25 ms   | 25.56 ms   | 19 ms      | 25 ms      | 100%         |
|                     | 10000 | 2m       | 516.72/s | 12.36 ms | 1.19 ms  | 233.87 ms | 21.99 ms   | 33.68 ms   | 21 ms      | 33 ms      | 100%         |
| test_admin_usage.js | 100   | 30s      | 7.21/s   | 4.74 ms  | 1.03 ms  | 141.30 ms | 4.91 ms    | 12.06 ms   | 4 ms       | 11 ms      | 100%         |
|                     | 500   | 30s      | 34.95/s  | 5.20 ms  | 0.81 ms  | 133.23 ms | 5.99 ms    | 11.30 ms   | 5 ms       | 10 ms      | 100%         |
|                     | 1000  | 30s      | 69.87/s  | 4.98 ms  | 0.73 ms  | 173.86 ms | 5.20 ms    | 10.75 ms   | 11 ms      | 13 ms      | 100%         |

> **Notes:**
> - R.D. = Response Duration
> - N.L. = Network Latency
> - `p(90)` and `p(95)` refer to the 90th and 95th percentile response times respectively.

**Click [here](/performance_test/performance_tests.md) for detailed screenshots and graphs of each test run.**

## k6 Installation For Performance Testing

### **Install k6 from GitHub Releases**

To install **k6** on your machine using GitHub releases, follow these steps:

1. **Download the latest k6 release**

   Go to the [k6 GitHub Releases page](https://github.com/grafana/k6/releases) and find the latest release. At the time of writing, we're focusing on **k6 v0.58.0**. You can download the `.tar.gz` or `.zip` file for your operating system (Linux, macOS, or Windows).

   Alternatively, run the following commands to download the release directly:

   ```sh
   curl -LO https://github.com/grafana/k6/releases/download/v0.58.0/k6-v0.58.0-linux-amd64.tar.gz
   ```

2. **Extract the downloaded file**

   After downloading, extract the file:

   ```sh
   tar -xvzf k6-v0.58.0-linux-amd64.tar.gz
   ```

3. **Move the k6 binary to `~/bin` and add it to your PATH**

   Move the `k6` binary to a directory included in your `PATH`:

   ```sh
   mv k6-v0.58.0-linux-amd64/k6 ~/bin
   ```
   
   Next ensure you have `export PATH=$HOME/bin:$PATH` added at the end of your `.bashrc` file. To do so, edit the file using nano:
   
   ```sh
   nano ~/.bashrc
   ```
   
   To execute the changes:

   ```sh
   source ~/.bashrc
   ```

4. **Verify the installation**

   To verify that k6 has been installed correctly, run:

   ```sh
   k6 version
   ```

### **Running k6 Scripts**

Before running the scripts, ensure that the API is live!

```sh
mvn package
java -jar target/riderapp-1.0.jar
```

After installing k6, you can use it to run load testing scripts. Here's how to run the scripts. First edit the options to reflect the type of test you want to run, and adjust the VU count. Then in your terminal, run the following command:

```sh
cd performance_test
k6 run register_drivers.js
```

**OR**

```sh
cd performance_test
k6 run test_ride_flow.js
```

This will execute the script, which makes requests to the API and imitate how a real user traffic would be. k6 will then display the results in your terminal after the test completes, showing you important metrics like the request rate, response time, and more.