package payment;

// Represents a cash payment method
public class CashPayment implements Payment {

    // Process a cash payment
    @Override
    public boolean pay(double amount) {

        // Payment amount must be greater than zero
        if (amount <= 0) {
            return false;
        }

        return true;
    }
}