package org.example.models;

import org.example.services.impl.CardPayment;
import org.example.services.impl.CashPayment;
import org.example.services.impl.UpiPayment;
import org.example.services.impl.WalletPayment;
import org.example.services.IPayment;


public enum PaymentMethodType {
    CASH, CARD, UPI, WALLET;

    public IPayment getPaymentMethod() {
        return switch (this) {
            case CASH -> new CashPayment();
            case CARD -> new CardPayment();
            case UPI -> new UpiPayment();
            case WALLET -> new WalletPayment();
        };
    }
}

