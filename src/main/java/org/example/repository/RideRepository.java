package org.example.repository;

import org.example.models.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    @Query("SELECT r.id, r.driver.id, r.destination, r.bill, r.timeTakenInMins, r.payment.paymentMethodType FROM Ride r WHERE r.rider.id = :riderId AND r.payment.paymentStatus != 'FAILED' ORDER BY r.rideID DESC")
    List<Object[]> findAllRides(@Param("riderId") long riderId);
}
