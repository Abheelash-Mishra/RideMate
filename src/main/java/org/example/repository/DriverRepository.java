package org.example.repository;

import org.example.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    @Query(value = "SELECT * FROM driver LIMIT :limit", nativeQuery = true)
    List<Driver> findFirstNDrivers(int limit);
}

