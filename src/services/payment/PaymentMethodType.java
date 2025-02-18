package services.payment;

import database.Database;
import services.payment.payment_impl.*;

public enum PaymentMethodType {
    CASH, CARD, UPI, WALLET;

    public Payment getPaymentMethod(Database db) {
        return switch (this) {
            case CASH -> new CashPayment(db);
            case CARD -> new CardPayment(db);
            case UPI -> new UpiPayment(db);
            case WALLET -> new WalletPayment(db);
        };
    }
}

