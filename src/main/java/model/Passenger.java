package model;

import enums.UserRole;

public class Passenger extends User {

    // 1. Attribute - store the passenger's current wallet balance
    private double balance;

    // 2. Constructor - initialize passenger information and starting balance
    public Passenger(String userId, String name, String email, String password) {

        // Call the parent User constructor
        super(userId, name, email, password, UserRole.PASSENGER);

        // Every new passenger starts with zero balance
        this.balance = 0.0;
    }

    // 3. Getter method - allow the current balance to be viewed
    public double getBalance() {
        return balance;
    }

    // 4. Top-up method - add money into the passenger's wallet
    public void topUp(double amount) {

        if (amount > 0) {
            balance += amount;

            System.out.println("Top up successful.");
            System.out.printf("Current balance: RM %.2f%n", balance);
        } else {
            System.out.println("Top up amount must be greater than RM 0.00.");
        }
    }
}