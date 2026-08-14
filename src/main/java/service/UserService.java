package service;

import exception.InvalidLoginException;
import model.User;

import java.util.HashMap;

/**
 * Handles user-related operations such as registration,
 * login, and viewing registered users.
 */
public class UserService {

    // 1. User collection - store registered users using email as the key
    private HashMap<String, User> users;

    // 2. Constructor - initialize an empty user collection
    public UserService() {
        this.users = new HashMap<>();
    }

    // 3. Register user - add a new user if the email is not already registered
    public void registerUser(User user) {

        String email = user.getEmail();

        // Prevent registration if the email is already used
        if (users.containsKey(email)) {
            System.out.println("Registration failed: Email is already registered.");
            return;
        }

        // Store the email as the key and the User object as the value
        users.put(email, user);

        System.out.println("Registration successful.");
    }

    // 4. Login - return the matching user if the credentials are correct
    public User login(String email, String password)
            throws InvalidLoginException {

        // Retrieve the user using email as the HashMap key
        User user = users.get(email);

        // Check that the user exists and the password is correct
        if (user != null && user.login(email, password)) {
            return user;
        }

        // Throw a custom exception when login credentials are incorrect
        throw new InvalidLoginException("Invalid email or password.");
    }

    // 5. View all users - display the profile of every registered user
    public void viewAllUsers() {

        // Display a message if no users are currently registered
        if (users.isEmpty()) {
            System.out.println("No registered users found.");
            return;
        }

        System.out.println("===== REGISTERED USERS =====");

        // Loop through every User object stored in the HashMap
        for (User user : users.values()) {
            user.viewProfile();
            System.out.println("----------------------------");
        }
    }
}