package payment;

import service.PaymentService;

// Final functionality test for the Payment module
public class PaymentFinalTest {

    public static void main(String[] args) {

        PaymentService paymentService = new PaymentService();

        // Use Payment references to demonstrate polymorphism
        Payment cashPayment = new CashPayment();
        Payment cardPayment = new CardPayment("1234567890123456");

        System.out.println("===== FINAL PAYMENT FUNCTIONALITY TEST =====");

        // Cash payment tests
        System.out.println("1. Valid cash payment: "
                + paymentService.processPayment(cashPayment, 10.00));

        System.out.println("2. Zero cash payment: "
                + paymentService.processPayment(cashPayment, 0));

        System.out.println("3. Negative cash payment: "
                + paymentService.processPayment(cashPayment, -10.00));

        // Card payment tests
        System.out.println("4. Valid card payment: "
                + paymentService.processPayment(cardPayment, 20.00));

        System.out.println("5. Zero card payment: "
                + paymentService.processPayment(cardPayment, 0));

        System.out.println("6. Negative card payment: "
                + paymentService.processPayment(cardPayment, -20.00));

        // Invalid card number tests
        Payment blankCard = new CardPayment("");
        System.out.println("7. Empty card number: "
                + paymentService.processPayment(blankCard, 20.00));

        Payment spaceCard = new CardPayment("   ");
        System.out.println("8. Blank-space card number: "
                + paymentService.processPayment(spaceCard, 20.00));

        Payment nullCard = new CardPayment(null);
        System.out.println("9. Null card number: "
                + paymentService.processPayment(nullCard, 20.00));

        // PaymentService validation
        System.out.println("10. Null payment method: "
                + paymentService.processPayment(null, 20.00));
    }
}