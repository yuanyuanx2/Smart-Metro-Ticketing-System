package service;

import model.User;

import java.util.HashMap;

/**
 * Handles user-related operations such as registration,
 * login, and viewing registered users.
 */
public class UserService {

    // 1. User collection - store users using email as the unique key
    private HashMap<String, User> users;

    // 2. Constructor - initialize an empty user collection
    public UserService() {
        this.users = new HashMap<>();
    }

    // 3. Register user - add a new user if the email is not already registered
    public void registerUser(User user) {

        String email = user.getEmail();

        if (users.containsKey(email)) {
            System.out.println("Registration failed: Email is already registered.");
            return;
        }

        users.put(email, user);

        System.out.println("Registration successful.");
    }
}