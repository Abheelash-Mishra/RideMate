# Overview

RiderApp is a Java-based ride management system that facilitates booking, tracking, and completing rides. It ensures
seamless interactions between riders and drivers while maintaining structured ride operations.

## Class Structure

**1. InMemoryDB:** Class that manages any object storage for other classes to access.

**2. RideService:** Manages ride-related operations such as:

- Adding riders to the service
- Handling ride creation
- Matching drivers to riders
- Billing the rider on completion

**3. DriverService:** Manages the driver objects and adds them to memory for `RideService` to utilize.

**4. PaymentService:** Handles payment of rides and wallet related functions for every rider.

**5. AdminService:** Handles any admin related commands.

**6. Driver:** Manages the details of each driver, and updates it availability.

**7. Rider:** Manages the details of each rider

**8. Ride:** Manages the details of each new ride and stores information when the ride is finished for billing.

**9. DistanceUtility:** Utility class that calculates the distance between 2 points.

**10. RiderAppTests:** Automated testcases using `JUnit 5`, testing various scenarios and points-of-failures. (Resets
`InMemoryDB` between each testcase)
