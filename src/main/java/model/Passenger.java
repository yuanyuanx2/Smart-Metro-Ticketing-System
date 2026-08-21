package model;

import enums.UserRole;

public class Passenger extends User {

    // Store the passenger's current wallet balance
    private double balance;

    /**
     * Creates a new passenger with zero starting balance.
     */
    public Passenger(String userId, String name,
                     String email, String password) {

        super(userId, name, email, password, UserRole.PASSENGER);
        this.balance = 0.0;
    }

    /**
     * Creates a passenger with an existing balance.
     * Used when restoring passenger data from a file.
     */
    public Passenger(String userId, String name,
                     String email, String password,
                     double balance) {

        super(userId, name, email, password, UserRole.PASSENGER);
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    /**
     * Adds money into the passenger's wallet.
     */
    public void topUp(double amount) {

        if (amount > 0) {
            balance += amount;

            System.out.println("Top up successful.");
            System.out.printf(
                    "Current balance: RM %.2f%n",
                    balance
            );

        } else {

            System.out.println(
                    "Top up amount must be greater than RM 0.00."
            );
        }
    }

    /**
     * Deducts the ticket fare from the passenger's wallet.
     */
    public void buyTicket(Ticket ticket) {

        if (balance < ticket.getFare()) {
            throw new IllegalArgumentException(
                    "Insufficient balance."
            );
        }

        balance -= ticket.getFare();
    }
}