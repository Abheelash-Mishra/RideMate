package org.example.repository;

import org.example.models.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    @Query("SELECT t.transactionID, t.amount, t.rechargeMethodType FROM WalletTransaction t WHERE t.rider.id = :riderID ORDER BY t.transactionID DESC")
    List<Object[]> findAllTransactionsByRiderID(@Param("riderID") long riderID);
}
