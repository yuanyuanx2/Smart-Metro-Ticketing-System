package service;

import payment.Payment;

// Handles payment processing using different payment methods
public class PaymentService {

    // Process payment using the selected payment method
    public boolean processPayment(Payment payment, double amount) {

        // Payment method must be provided
        if (payment == null) {
            return false;
        }

        return payment.pay(amount);
    }
}