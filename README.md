# Overview

RiderApp is a Java-based ride management system that facilitates booking, tracking, and completing rides. It ensures
seamless interactions between riders and drivers while maintaining structured ride operations.

## Class Structure

**1. RideService:** Manages ride-related operations such as:

- Adding riders to the service
- Handling ride creation
- Matching drivers to riders
- Billing the rider on completion

**2. DriverService:** Manages the driver objects and adds them to memory for `RideService` to utilize.

**3. Driver:** Manages the details of each driver, and updates it availability.

**4. Rider:** Manages the details of each rider

**5. Ride:** Manages the details of each new ride and stores information when the ride is finished for billing.

**6. DistanceUtility:** Utility class that calculates the distance between 2 points.

**7. RiderAppTests:** Automated testcases using `JUnit`, testing various scenarios and points-of-failures. (Resets
`RideService` and `DriverService` between each testcase)
