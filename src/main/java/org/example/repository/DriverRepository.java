package org.example.repository;

import org.example.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query(value = "SELECT * FROM driver LIMIT :limit", nativeQuery = true)
    List<Driver> findFirstNDrivers(int limit);

    @Query("""
        SELECT d.driverID FROM Driver d
        WHERE d.available = true
        AND POWER(d.x_coordinate - :x, 2) + POWER(d.y_coordinate - :y, 2) <= POWER(:limit, 2)
        ORDER BY POWER(d.x_coordinate - :x, 2) + POWER(d.y_coordinate - :y, 2) ASC
    """)
    List<Long> findNearbyDrivers(@Param("x") int x, @Param("y") int y, @Param("limit") double limit, Pageable pageable);
}

