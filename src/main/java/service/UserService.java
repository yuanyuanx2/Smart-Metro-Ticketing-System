package service;

import exception.InvalidLoginException;
import model.User;

import java.util.HashMap;

/**
 * Handles user-related operations such as registration,
 * login, and viewing registered users.
 */
public class UserService {

    // 1. User collection - store registered users using user ID as the stable key
    private HashMap<String, User> users;

    // 2. Constructor - initialize an empty user collection
    public UserService() {
        this.users = new HashMap<>();
    }

    // 3. Register user - validate and add a new user to the collection
    public void registerUser(User user) {

        String userId = user.getUserId();
        String name = user.getName();
        String email = user.getEmail();

        // Reject reserved "|" character because it will be used for TXT file storage
        if (userId.contains("|") || name.contains("|") || email.contains("|")) {
            System.out.println("Registration failed: The character '|' is not allowed.");
            return;
        }

        // Validate that the email follows a basic email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            System.out.println("Registration failed: Invalid email format.");
            return;
        }

        // Prevent registration if the user ID is already used
        if (users.containsKey(userId)) {
            System.out.println("Registration failed: User ID is already registered.");
            return;
        }

        // Prevent registration if the email is already used
        for (User existingUser : users.values()) {
            if (existingUser.getEmail().equalsIgnoreCase(email)) {
                System.out.println("Registration failed: Email is already registered.");
                return;
            }
        }

        // Store the user ID as the key and the User object as the value
        users.put(userId, user);

        System.out.println("Registration successful.");
    }

    // 4. Login - return the matching user if the credentials are correct
    public User login(String email, String password)
            throws InvalidLoginException {

        // Search through registered users for matching login credentials
        for (User user : users.values()) {

            if (user.login(email, password)) {
                return user;
            }
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