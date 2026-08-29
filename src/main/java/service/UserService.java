package service;

import exception.InvalidLoginException;
import model.User;

import java.util.HashMap;

/**
 * Handles user-related operations such as registration,
 * login, and viewing registered users.
 */
public class UserService {

    // Store registered users using user ID as the stable key
    private HashMap<String, User> users;

    /**
     * Creates an empty User collection.
     */
    public UserService() {
        this.users = new HashMap<>();
    }

    /**
     * Creates a UserService using Users
     * restored from file storage.
     */
    public UserService(HashMap<String, User> users) {
        this.users = users;
    }

    /**
     * Validates and registers a new User.
     */
    public void registerUser(User user) {

        if (user == null) {

            System.out.println(
                    "Registration failed: User cannot be null."
            );

            return;
        }

        String userId =
                user.getUserId();

        String name =
                user.getName();

        String email =
                user.getEmail();

        String password =
                user.getPassword();

        /*
         * "|" is reserved as the TXT file delimiter.
         */
        if (userId.contains("|")
                || name.contains("|")
                || email.contains("|")
                || password.contains("|")) {

            System.out.println(
                    "Registration failed: The character '|' is not allowed."
            );

            return;
        }

        /*
         * Validate basic email format.
         */
        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )) {

            System.out.println(
                    "Registration failed: Invalid email format."
            );

            return;
        }

        /*
         * User IDs must be unique.
         */
        if (users.containsKey(userId)) {

            System.out.println(
                    "Registration failed: User ID is already registered."
            );

            return;
        }

        /*
         * Email addresses must be unique.
         */
        for (User existingUser : users.values()) {

            if (existingUser.getEmail()
                    .equalsIgnoreCase(email)) {

                System.out.println(
                        "Registration failed: Email is already registered."
                );

                return;
            }
        }

        users.put(
                userId,
                user
        );

        System.out.println(
                "Registration successful."
        );
    }

    /**
     * Returns the matching User
     * when login credentials are correct.
     */
    public User login(
            String email,
            String password)
            throws InvalidLoginException {

        for (User user : users.values()) {

            if (user.login(
                    email,
                    password
            )) {

                return user;
            }
        }

        throw new InvalidLoginException(
                "Invalid email or password."
        );
    }

    /**
     * Displays all registered Users.
     */
    public void viewAllUsers() {

        if (users.isEmpty()) {

            System.out.println(
                    "No registered users found."
            );

            return;
        }

        System.out.println(
                "===== REGISTERED USERS ====="
        );

        for (User user : users.values()) {

            user.viewProfile();

            System.out.println(
                    "----------------------------"
            );
        }
    }
}