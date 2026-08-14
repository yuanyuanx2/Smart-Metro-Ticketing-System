package model;

import enums.UserRole;

/**
 * Represents an administrator in the Smart Metro Ticketing System.
 * Admin inherits the common user information and behaviours
 * from the User class.
 */
public class Admin extends User {

    // 1. Constructor - initialize an administrator account
    public Admin(String userId, String name, String email, String password) {

        // Call the parent User constructor and automatically assign ADMIN role
        super(userId, name, email, password, UserRole.ADMIN);
    }
}