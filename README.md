# RiderApp

RiderApp is a console-based and REST API-enabled ride-hailing application written in Java using **Spring**. It supports both **CLI commands** and **REST API endpoints** for managing rides, drivers, and riders.

## Features
- **CLI Mode**: Interact with the system through terminal commands.
- **REST API Mode**: Use HTTP requests to manage rides and users.
- **Spring MVC**: Configured with XML-based and annotation-based Spring setup.
- **In-Memory Database**: Stores data using a `Database` interface implementation.
- **Flexible Payment System**: Supports multiple payment methods via interfaces.
- **Unit & Integration Testing**: Tested using JUnit and MockMvc.

---
## Installation & Setup
### **1. Clone the Repository**
```sh
git clone https://github.com/Abheelash-Mishra/riderapp.git
cd riderapp
```

### **2. Install Dependencies**
Ensure you have **Maven** installed. Then, run:
```sh
mvn clean install
```

### **3. Running in CLI Mode**
Run `RiderApp` (main class) and use CLI commands like:

```sh
ADD_DRIVER D1 1 1
ADD_DRIVER D2 4 5
ADD_DRIVER D3 2 2
ADD_RIDER R1 0 0
MATCH R1
START_RIDE RIDE-001 2 R1
STOP_RIDE RIDE-001 4 5 32
BILL RIDE-001
```

### **4. Running as a REST API**
#### **Deploy the WAR File**
Since RiderApp is packaged as a WAR, deploy it to **Apache Tomcat**:
1. Copy `target/riderapp.war` to Tomcat's `webapps` directory:
   ```sh
   cp target/riderapp-1.0.war /path/to/tomcat/webapps/
   ```
2. Start Tomcat:
   ```sh
   /path/to/tomcat/bin/startup.sh
   ```
3. Access the API at `http://localhost:8080/riderapp/`

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
curl -X GET http://localhost:8080/riderapp/admin/drivers
```

---
## Testing
#### **Run Unit Tests**
```sh
mvn test
```
#### **Run Integration Tests**
```sh
mvn verify
```

