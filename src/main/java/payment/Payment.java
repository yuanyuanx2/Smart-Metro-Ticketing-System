package payment;

// Defines a common payment method for all types of payment
public interface Payment {

    // Process the payment and return whether it is successful
    boolean pay(double amount);
}