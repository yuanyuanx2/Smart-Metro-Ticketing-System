package payment;

import service.PaymentService;

// Tests the Payment module
public class PaymentTest {

    public static void main(String[] args) {

        PaymentService paymentService = new PaymentService();

        System.out.println("===== PAYMENT COMPONENT TEST =====");

        // Test 1: Valid cash payment
        Payment cashPayment = new CashPayment();
        System.out.println("1. Valid cash payment: "
                + paymentService.processPayment(cashPayment, 10.00));

        // Test 2: Zero cash payment
        System.out.println("2. Zero cash payment: "
                + paymentService.processPayment(cashPayment, 0));

        // Test 3: Negative cash payment
        System.out.println("3. Negative cash payment: "
                + paymentService.processPayment(cashPayment, -10.00));

        // Test 4: Valid card payment
        Payment cardPayment = new CardPayment("1234567890123456");
        System.out.println("4. Valid card payment: "
                + paymentService.processPayment(cardPayment, 20.00));

        // Test 5: Blank card number
        Payment blankCard = new CardPayment("");
        System.out.println("5. Blank card number: "
                + paymentService.processPayment(blankCard, 20.00));

        // Test 6: Invalid card payment amount
        System.out.println("6. Negative card payment: "
                + paymentService.processPayment(cardPayment, -20.00));

        // Test 7: No payment method
        System.out.println("7. Null payment method: "
                + paymentService.processPayment(null, 20.00));
    }
}