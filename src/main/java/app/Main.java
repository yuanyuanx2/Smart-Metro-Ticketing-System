package app;

import enums.UserRole;
import exception.FileProcessingException;
import exception.InvalidLoginException;
import fare.StandardFareCalculator;
import model.Passenger;
import model.Route;
import model.Station;
import model.Ticket;
import model.Train;
import model.User;
import repository.TXTFileManager;
import service.RouteService;
import service.StationService;
import service.TicketService;
import service.TrainService;
import service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Main entry point for the Smart Metro Ticketing System.
 */
public class Main {

    private static final Scanner scanner =
            new Scanner(System.in);

    private static final String USERS_FILE =
            "src/main/resources/data/users.txt";

    private static final String STATIONS_FILE =
            "src/main/resources/data/stations.txt";

    private static final String TRAINS_FILE =
            "src/main/resources/data/trains.txt";

    private static final String ROUTES_FILE =
            "src/main/resources/data/routes.txt";

    private static final String TICKETS_FILE =
            "src/main/resources/data/tickets.txt";

    /*
     * One shared TXTFileManager is required so that
     * loaded Routes and Tickets reuse the correct
     * Station and Passenger objects.
     */
    private static final TXTFileManager fileManager =
            new TXTFileManager();

    private static final HashMap<String, User> users =
            new HashMap<>();

    private static final ArrayList<Ticket> tickets =
            new ArrayList<>();

    private static UserService userService;
    private static StationService stationService;
    private static TrainService trainService;
    private static RouteService routeService;
    private static TicketService ticketService;

    public static void main(String[] args) {

        /*
         * Important relationship/load order:
         *
         * 1. Users
         * 2. Stations
         * 3. Trains
         * 4. Routes
         * 5. Tickets
         */
        loadUsers();
        loadStations();
        loadTrains();
        loadRoutes();
        loadTickets();

        boolean running = true;

        while (running) {

            displayMainMenu();

            System.out.print("Enter choice: ");

            String choice =
                    scanner.nextLine().trim();

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
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }

        clearScreen();

        System.out.println(
                "Thank you for using Smart Metro Ticketing System."
        );

        scanner.close();
    }

    /**
     * Loads users from TXT storage.
     */
    private static void loadUsers() {

        users.clear();

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

        } catch (FileProcessingException e) {

            clearScreen();

            System.out.println(
                    "Unable to load user data: "
                            + e.getMessage()
            );

            System.out.println(
                    "The system will continue with an empty user list."
            );

            waitForBack();
        }

        userService =
                new UserService(users);
    }

    /**
     * Loads stations from TXT storage.
     */
    private static void loadStations() {

        stationService =
                new StationService();

        try {

            Object loadedData =
                    fileManager.loadData(STATIONS_FILE);

            if (loadedData instanceof ArrayList<?> loadedStations) {

                for (Object item : loadedStations) {

                    if (item instanceof Station station) {

                        stationService.addStation(
                                station
                        );
                    }
                }
            }

            stationService.sortStationsByName();

        } catch (FileProcessingException e) {

            clearScreen();

            System.out.println(
                    "Unable to load station data: "
                            + e.getMessage()
            );

            System.out.println(
                    "The system will continue with an empty station list."
            );

            waitForBack();
        }
    }

    /**
     * Loads trains from TXT storage.
     */
    private static void loadTrains() {

        trainService =
                new TrainService();

        try {

            Object loadedData =
                    fileManager.loadData(TRAINS_FILE);

            if (loadedData instanceof ArrayList<?> loadedTrains) {

                for (Object item : loadedTrains) {

                    if (item instanceof Train train) {

                        trainService.addTrain(
                                train
                        );
                    }
                }
            }

        } catch (FileProcessingException e) {

            clearScreen();

            System.out.println(
                    "Unable to load train data: "
                            + e.getMessage()
            );

            System.out.println(
                    "The system will continue with an empty train list."
            );

            waitForBack();
        }
    }

    /**
     * Loads routes from TXT storage.
     */
    private static void loadRoutes() {

        routeService =
                new RouteService();

        try {

            Object loadedData =
                    fileManager.loadData(ROUTES_FILE);

            if (loadedData instanceof ArrayList<?> loadedRoutes) {

                for (Object item : loadedRoutes) {

                    if (item instanceof Route route) {

                        routeService.addRoute(
                                route
                        );
                    }
                }
            }

        } catch (FileProcessingException e) {

            clearScreen();

            System.out.println(
                    "Unable to load route data: "
                            + e.getMessage()
            );

            System.out.println(
                    "The system will continue with an empty route list."
            );

            waitForBack();
        }
    }

    /**
     * Loads tickets from TXT storage.
     *
     * The shared TXTFileManager ensures each loaded
     * Ticket uses the existing Passenger and Station
     * objects that were loaded earlier.
     */
    private static void loadTickets() {

        tickets.clear();

        try {

            Object loadedData =
                    fileManager.loadData(TICKETS_FILE);

            if (loadedData instanceof ArrayList<?> loadedTickets) {

                for (Object item : loadedTickets) {

                    if (item instanceof Ticket ticket) {

                        tickets.add(
                                ticket
                        );
                    }
                }
            }

        } catch (FileProcessingException e) {

            clearScreen();

            System.out.println(
                    "Unable to load ticket data: "
                            + e.getMessage()
            );

            System.out.println(
                    "The system will continue with an empty ticket list."
            );

            waitForBack();
        }

        ticketService =
                new TicketService(
                        new StandardFareCalculator(),
                        tickets
                );
    }

    /**
     * Registers a new Passenger account.
     */
    private static void registerPassenger() {

        clearScreen();

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

        if (userId.isBlank()
                || name.isBlank()
                || email.isBlank()
                || password.isBlank()) {

            System.out.println();

            System.out.println(
                    "Registration failed: "
                            + "All fields are required."
            );

            waitForBack();

            return;
        }

        Passenger passenger =
                new Passenger(
                        userId,
                        name,
                        email,
                        password
                );

        System.out.println();

        userService.registerUser(
                passenger
        );

        waitForBack();
    }

    /**
     * Handles Passenger and Admin login.
     */
    private static void login() {

        clearScreen();

        System.out.println(
                "========== LOGIN =========="
        );

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

            if (user.getRole()
                    == UserRole.PASSENGER) {

                passengerMenu(
                        (Passenger) user
                );

            } else if (user.getRole()
                    == UserRole.ADMIN) {

                clearScreen();

                System.out.println(
                        "Login successful. Welcome, "
                                + user.getName()
                                + "!"
                );

                System.out.println();

                System.out.println(
                        "Admin menu will be connected later."
                );

                waitForBack();
            }

        } catch (InvalidLoginException e) {

            clearScreen();

            System.out.println(
                    "Login failed: "
                            + e.getMessage()
            );

            waitForBack();
        }
    }

    /**
     * Displays and handles the Passenger menu.
     */
    private static void passengerMenu(
            Passenger passenger) {

        boolean loggedIn = true;

        while (loggedIn) {

            displayPassengerMenu(
                    passenger
            );

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    viewPassengerProfile(
                            passenger
                    );
                    break;

                case "2":
                    topUpBalance(
                            passenger
                    );
                    break;

                case "3":
                    viewStations();
                    break;

                case "4":
                    viewRoutes();
                    break;

                case "5":
                    viewPassengerTickets(
                            passenger
                    );
                    break;

                case "0":
                    loggedIn = false;
                    break;

                default:
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    /**
     * Displays the Passenger menu.
     */
    private static void displayPassengerMenu(
            Passenger passenger) {

        clearScreen();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "            PASSENGER MENU"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "Welcome, "
                        + passenger.getName()
        );

        System.out.printf(
                "Balance: RM %.2f%n",
                passenger.getBalance()
        );

        System.out.println();

        System.out.println(
                "1. View Profile"
        );

        System.out.println(
                "2. Top Up Balance"
        );

        System.out.println(
                "3. View Stations"
        );

        System.out.println(
                "4. View Routes"
        );

        System.out.println(
                "5. View My Tickets"
        );

        System.out.println(
                "0. Logout"
        );

        System.out.println(
                "========================================"
        );
    }

    /**
     * Displays the Passenger profile.
     */
    private static void viewPassengerProfile(
            Passenger passenger) {

        clearScreen();

        System.out.println(
                "===== PASSENGER PROFILE ====="
        );

        passenger.viewProfile();

        System.out.printf(
                "Balance : RM %.2f%n",
                passenger.getBalance()
        );

        waitForBack();
    }

    /**
     * Handles Passenger wallet top up.
     */
    private static void topUpBalance(
            Passenger passenger) {

        clearScreen();

        System.out.println(
                "===== TOP UP BALANCE ====="
        );

        System.out.printf(
                "Current balance: RM %.2f%n",
                passenger.getBalance()
        );

        System.out.print(
                "Enter top up amount: RM "
        );

        String input =
                scanner.nextLine().trim();

        System.out.println();

        try {

            double amount =
                    Double.parseDouble(input);

            passenger.topUp(
                    amount
            );

        } catch (NumberFormatException e) {

            System.out.println(
                    "Invalid amount. "
                            + "Please enter a valid number."
            );
        }

        waitForBack();
    }

    /**
     * Displays available metro stations.
     */
    private static void viewStations() {

        clearScreen();

        System.out.println(
                "========== METRO STATIONS =========="
        );

        System.out.println();

        stationService.viewStations();

        waitForBack();
    }

    /**
     * Displays available metro routes.
     */
    private static void viewRoutes() {

        clearScreen();

        System.out.println(
                "========== METRO ROUTES =========="
        );

        System.out.println();

        routeService.viewRoutes();

        waitForBack();
    }

    /**
     * Displays only tickets belonging to
     * the currently logged-in Passenger.
     */
    private static void viewPassengerTickets(
            Passenger passenger) {

        clearScreen();

        System.out.println(
                "========== MY TICKETS =========="
        );

        System.out.println();

        boolean found = false;

        for (Ticket ticket : tickets) {

            /*
             * Loaded Tickets reuse the same Passenger
             * object, so this preserves the object
             * relationship rather than matching
             * duplicated passenger information.
             */
            if (ticket.getPassenger() == passenger) {

                ticket.printTicket();

                System.out.println(
                        "-------------------------"
                );

                found = true;
            }
        }

        if (!found) {

            System.out.println(
                    "No tickets found for this passenger."
            );
        }

        waitForBack();
    }

    /**
     * Displays the main application menu.
     */
    private static void displayMainMenu() {

        clearScreen();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "     SMART METRO TICKETING SYSTEM"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "1. Login"
        );

        System.out.println(
                "2. Register Passenger"
        );

        System.out.println(
                "0. Exit"
        );

        System.out.println(
                "========================================"
        );
    }

    /**
     * Displays a message and waits for the user.
     */
    private static void showMessage(
            String message) {

        clearScreen();

        System.out.println(
                message
        );

        waitForBack();
    }

    /**
     * Waits until X is entered.
     */
    private static void waitForBack() {

        while (true) {

            System.out.println();

            System.out.print(
                    "Press X to go back: "
            );

            String choice =
                    scanner.nextLine().trim();

            if (choice.equalsIgnoreCase("X")) {
                return;
            }

            System.out.println(
                    "Invalid choice. Press X to go back."
            );
        }
    }

    /**
     * Clears the visible console.
     */
    private static void clearScreen() {

        System.out.print(
                "\033[H\033[2J"
        );

        System.out.flush();
    }
}