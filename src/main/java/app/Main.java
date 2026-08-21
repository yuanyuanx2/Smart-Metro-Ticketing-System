package app;

import enums.UserRole;
import exception.FileProcessingException;
import exception.InvalidLoginException;
import model.Passenger;
import model.User;
import repository.TXTFileManager;
import service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Main entry point for the Smart Metro Ticketing System.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final String USERS_FILE =
            "src/main/resources/data/users.txt";

    // Shared user collection used by UserService and later file saving.
    private static final HashMap<String, User> users =
            new HashMap<>();

    private static UserService userService;

    public static void main(String[] args) {

        // Load existing users when the application starts.
        loadUsers();

        boolean running = true;

        while (running) {

            displayMainMenu();

            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    login();
                    break;

                case "2":
                    registerPassenger();
                    break;

                case "0":
                    running = false;
                    break;

                default:
                    System.out.println(
                            "\nInvalid choice. Please try again."
                    );
                    pause();
            }
        }

        System.out.println(
                "\nThank you for using Smart Metro Ticketing System."
        );

        scanner.close();
    }

    /**
     * Loads users from TXT storage and creates the UserService.
     */
    private static void loadUsers() {

        users.clear();

        TXTFileManager fileManager =
                new TXTFileManager();

        try {

            Object loadedData =
                    fileManager.loadData(USERS_FILE);

            if (loadedData instanceof ArrayList<?> loadedUsers) {

                for (Object item : loadedUsers) {

                    if (item instanceof User user) {

                        users.put(
                                user.getUserId(),
                                user
                        );
                    }
                }
            }

            System.out.println(
                    "User data loaded successfully."
            );

        } catch (FileProcessingException e) {

            System.out.println(
                    "Unable to load user data: "
                            + e.getMessage()
            );

            System.out.println(
                    "The system will continue with an empty user list."
            );
        }

        userService =
                new UserService(users);
    }

    /**
     * Registers a new Passenger account.
     */
    private static void registerPassenger() {

        System.out.println();
        System.out.println(
                "===== PASSENGER REGISTRATION ====="
        );

        System.out.print("Passenger ID: ");
        String userId =
                scanner.nextLine().trim();

        System.out.print("Name: ");
        String name =
                scanner.nextLine().trim();

        System.out.print("Email: ");
        String email =
                scanner.nextLine().trim();

        System.out.print("Password: ");
        String password =
                scanner.nextLine();

        // Prevent empty registration fields.
        if (userId.isBlank()
                || name.isBlank()
                || email.isBlank()
                || password.isBlank()) {

            System.out.println(
                    "Registration failed: "
                            + "All fields are required."
            );

            pause();
            return;
        }

        Passenger passenger =
                new Passenger(
                        userId,
                        name,
                        email,
                        password
                );

        userService.registerUser(passenger);

        pause();
    }

    /**
     * Handles Passenger and Admin login.
     */
    private static void login() {

        System.out.println();
        System.out.println("========== LOGIN ==========");

        System.out.print("Email: ");
        String email =
                scanner.nextLine().trim();

        System.out.print("Password: ");
        String password =
                scanner.nextLine();

        try {

            User user =
                    userService.login(
                            email,
                            password
                    );

            System.out.println();
            System.out.println(
                    "Login successful. Welcome, "
                            + user.getName()
                            + "!"
            );

            if (user.getRole()
                    == UserRole.PASSENGER) {

                System.out.println(
                        "Passenger menu will be connected later."
                );

            } else if (user.getRole()
                    == UserRole.ADMIN) {

                System.out.println(
                        "Admin menu will be connected later."
                );
            }

        } catch (InvalidLoginException e) {

            System.out.println();
            System.out.println(
                    "Login failed: "
                            + e.getMessage()
            );
        }

        pause();
    }

    /**
     * Displays the main application menu.
     */
    private static void displayMainMenu() {

        System.out.println();
        System.out.println(
                "========================================"
        );
        System.out.println(
                "     SMART METRO TICKETING SYSTEM"
        );
        System.out.println(
                "========================================"
        );
        System.out.println("1. Login");
        System.out.println("2. Register Passenger");
        System.out.println("0. Exit");
        System.out.println(
                "========================================"
        );
    }

    /**
     * Waits for the user before returning to the menu.
     */
    private static void pause() {

        System.out.println();
        System.out.print(
                "Press Enter to continue..."
        );

        scanner.nextLine();
    }
}