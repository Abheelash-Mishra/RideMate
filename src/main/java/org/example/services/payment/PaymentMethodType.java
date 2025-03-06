package org.example.services.payment;

import org.example.repository.Database;
import org.example.services.payment.impl.*;


public enum PaymentMethodType {
    CASH, CARD, UPI, WALLET;

    public IPayment getPaymentMethod(Database db) {
        return switch (this) {
            case CASH -> new CashPayment(db);
            case CARD -> new CardPayment(db);
            case UPI -> new UpiPayment(db);
            case WALLET -> new WalletPayment(db);
        };
    }
}

