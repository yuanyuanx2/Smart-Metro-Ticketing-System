package payment;

// Represents a card payment method
public class CardPayment implements Payment {

    private String cardNumber;

    // Constructor to initialize the card number
    public CardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    // Process a card payment
    @Override
    public boolean pay(double amount) {

        // Payment amount must be greater than zero
        if (amount <= 0) {
            return false;
        }

        // Card number must be provided
        if (cardNumber == null || cardNumber.isBlank()) {
            return false;
        }

        return true;
    }
}