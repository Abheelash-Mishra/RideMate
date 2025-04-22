package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.PaymentDetailsDTO;
import org.example.dto.TransactionDetailsDTO;
import org.example.exceptions.InvalidRiderIDException;
import org.example.models.PaymentMethodType;
import org.example.models.Rider;
import org.example.repository.RiderRepository;
import org.example.repository.WalletTransactionRepository;
import org.example.services.PaymentType;
import org.example.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {
    private final HashMap<PaymentMethodType, PaymentType> paymentMethods;

    private PaymentType paymentMethod;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    public PaymentServiceImpl(List<PaymentType> paymentImplementations) {
        this.paymentMethods = new HashMap<>();

        for (PaymentType payment : paymentImplementations) {
            PaymentMethodType type = getPaymentType(payment);
            this.paymentMethods.put(type, payment);
        }

        // Default payment method
        this.paymentMethod = this.paymentMethods.get(PaymentMethodType.WALLET);
    }

    @Override
    public PaymentDetailsDTO processPayment(long rideID, PaymentMethodType paymentMethodType) {
        this.paymentMethod = this.paymentMethods.get(paymentMethodType);

        return paymentMethod.sendMoney(rideID);
    }

    @Override
    public float addMoney(long riderID, float amount, PaymentMethodType rechargeMethodType) {
        this.paymentMethod = this.paymentMethods.get(PaymentMethodType.WALLET);

        WalletPayment walletPayment = (WalletPayment) this.paymentMethod;

        return walletPayment.addMoney(riderID, amount, rechargeMethodType);
    }

    @Override
    public float getBalance(long riderID) {
        Rider rider = riderRepository.findById(riderID)
                .orElseThrow(() -> new InvalidRiderIDException("Invalid Rider ID - " + riderID + " || No such rider exists"));

        return rider.getWalletAmount();
    }

    @Override
    public List<TransactionDetailsDTO> getAllTransactions(long riderID) {
        try {
            List<Object[]> rawData = walletTransactionRepository.findAllTransactionsByRiderID(riderID);
            List<TransactionDetailsDTO> summaryList = new ArrayList<>();

            for (Object[] row : rawData) {
                TransactionDetailsDTO dto = new TransactionDetailsDTO(
                        (Long) row[0],
                        (Float) row[1],
                        (PaymentMethodType) row[2]
                );
                summaryList.add(dto);
            }

            return summaryList;
        } catch (Exception e) {
            log.error("Unexpected error while fetching all rides of rider '{}'", riderID);
            log.error("Exception: {}", e.getMessage(), e);

            throw new RuntimeException("Failed to fetch all rides of rider " + riderID, e);
        }
    }

    private PaymentMethodType getPaymentType(PaymentType payment) {
        if (payment instanceof WalletPayment) return PaymentMethodType.WALLET;
        if (payment instanceof CashPayment) return PaymentMethodType.CASH;
        if (payment instanceof CardPayment) return PaymentMethodType.CARD;
        if (payment instanceof UpiPayment) return PaymentMethodType.UPI;

        throw new IllegalArgumentException("Unknown Payment Type");
    }
}



