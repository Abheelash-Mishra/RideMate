# RiderApp

RiderApp is a ride-hailing application written in Java using **Spring Boot**. It supports **REST API endpoints** for managing rides, drivers, and riders.

## Features
- **REST API Mode**: Use HTTP requests to manage rides and users.
- **H2 Database**: Stores data using an in-memory database.
- **Flexible Payment System**: Supports multiple payment methods via interfaces.
- **Unit & Integration Testing**: Tested using JUnit and MockMvc.

---

## Models
![model_diagram.png](model_diagram.png)

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

### **3. Running as a REST API**
Since RiderApp is a Spring Boot application, it has an embedded Apache Tomcat server. Simply run the application by:
1. Running this command:
   ```sh
   mvn spring-boot:run
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
curl -X GET http://localhost:8080/ride/bill?rideID=RIDE-001
```

---
## Testing
#### **Run Tests**
```sh
mvn test
```

