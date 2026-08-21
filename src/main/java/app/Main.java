package app;

import java.util.Scanner;

/**
 * Main entry point for the Smart Metro Ticketing System.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        boolean running = true;

        while (running) {

            displayMainMenu();

            System.out.print("Enter choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    System.out.println(
                            "\nLogin feature will be connected in the next checkpoint."
                    );
                    pause();
                    break;

                case "2":
                    System.out.println(
                            "\nPassenger registration will be connected later."
                    );
                    pause();
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
     * Displays the main application menu.
     */
    private static void displayMainMenu() {

        System.out.println();
        System.out.println("========================================");
        System.out.println("     SMART METRO TICKETING SYSTEM");
        System.out.println("========================================");
        System.out.println("1. Login");
        System.out.println("2. Register Passenger");
        System.out.println("0. Exit");
        System.out.println("========================================");
    }

    /**
     * Waits for the user before returning to the menu.
     */
    private static void pause() {

        System.out.println();
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}