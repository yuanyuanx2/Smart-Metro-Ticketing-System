package app;

import enums.TicketStatus;
import enums.TicketType;
import enums.UserRole;
import exception.FileProcessingException;
import exception.InvalidLoginException;
import fare.FareCalculator;
import fare.StandardFareCalculator;
import model.Passenger;
import model.Route;
import model.Station;
import model.Ticket;
import model.Train;
import model.User;
import payment.CardPayment;
import payment.CashPayment;
import payment.Payment;
import repository.TXTFileManager;
import service.PaymentService;
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
     * Shared TXTFileManager.
     * This preserves Passenger/Station object relationships
     * when Routes and Tickets are restored.
     */
    private static final TXTFileManager fileManager =
            new TXTFileManager();

    private static final HashMap<String, User> users =
            new HashMap<>();

    private static final ArrayList<Route> routes =
            new ArrayList<>();

    private static final ArrayList<Ticket> tickets =
            new ArrayList<>();

    private static final FareCalculator fareCalculator =
            new StandardFareCalculator();

    private static final PaymentService paymentService =
            new PaymentService();

    private static UserService userService;
    private static StationService stationService;
    private static TrainService trainService;
    private static RouteService routeService;
    private static TicketService ticketService;

    public static void main(String[] args) {

        /*
         * Required loading relationship/order:
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

        routes.clear();

        routeService =
                new RouteService();

        try {

            Object loadedData =
                    fileManager.loadData(ROUTES_FILE);

            if (loadedData instanceof ArrayList<?> loadedRoutes) {

                for (Object item : loadedRoutes) {

                    if (item instanceof Route route) {

                        routes.add(
                                route
                        );

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
                        fareCalculator,
                        tickets
                );
    }

    /**
     * Registers a new Passenger.
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
                    "Registration failed: All fields are required."
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
     * Handles login.
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
     * Passenger menu.
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
                    buyTicket(
                            passenger
                    );
                    break;

                case "6":
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
     * Displays Passenger menu.
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
                "5. Buy Ticket"
        );

        System.out.println(
                "6. View My Tickets"
        );

        System.out.println(
                "0. Logout"
        );

        System.out.println(
                "========================================"
        );
    }

    /**
     * Displays Passenger profile.
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
     * Handles Passenger wallet top-up.
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
                    "Invalid amount. Please enter a valid number."
            );
        }

        waitForBack();
    }

    /**
     * Displays Stations.
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
     * Displays Routes.
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
     * Passenger ticket purchasing workflow.
     *
     * Route
     * -> Ticket Type
     * -> Fare
     * -> Active Ticket Warning
     * -> Balance Check
     * -> Payment
     * -> Ticket Creation
     *
     * Ticket creation only occurs AFTER
     * successful payment.
     */
    private static void buyTicket(
            Passenger passenger) {

        clearScreen();

        System.out.println(
                "========== BUY TICKET =========="
        );

        System.out.println();

        if (routes.isEmpty()) {

            System.out.println(
                    "No routes are currently available."
            );

            waitForBack();

            return;
        }

        routeService.viewRoutes();

        System.out.println();

        System.out.print(
                "Enter Route ID or X to go back: "
        );

        String routeId =
                scanner.nextLine().trim();

        if (routeId.equalsIgnoreCase("X")) {
            return;
        }

        Route selectedRoute =
                findRouteById(
                        routeId
                );

        if (selectedRoute == null) {

            showMessage(
                    "Route not found."
            );

            return;
        }

        TicketType ticketType =
                selectTicketType();

        if (ticketType == null) {
            return;
        }

        double fare =
                fareCalculator.calculateFare(
                        selectedRoute,
                        ticketType
                );

        /*
         * Warn Passenger if ACTIVE tickets
         * already exist.
         *
         * This warning does not automatically
         * reject the purchase.
         */
        boolean continuePurchase =
                confirmActiveTicketWarning(
                        passenger,
                        selectedRoute,
                        ticketType
                );

        if (!continuePurchase) {

            showMessage(
                    "Purchase cancelled."
            );

            return;
        }

        clearScreen();

        System.out.println(
                "========== PURCHASE SUMMARY =========="
        );

        System.out.println();

        System.out.println(
                "Route       : "
                        + selectedRoute
                        .getSource()
                        .getName()
                        + " -> "
                        + selectedRoute
                        .getDestination()
                        .getName()
        );

        System.out.println(
                "Ticket Type : "
                        + ticketType
        );

        System.out.printf(
                "Fare        : RM %.2f%n",
                fare
        );

        System.out.printf(
                "Balance     : RM %.2f%n",
                passenger.getBalance()
        );

        System.out.println();

        /*
         * Insufficient balance is checked
         * before Payment processing.
         */
        if (passenger.getBalance() < fare) {

            System.out.println(
                    "Insufficient balance."
            );

            System.out.println(
                    "Please top up your balance before purchasing."
            );

            waitForBack();

            return;
        }

        System.out.print(
                "Confirm purchase? (Y/N): "
        );

        String confirmation =
                scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")) {

            showMessage(
                    "Purchase cancelled."
            );

            return;
        }

        Payment payment =
                selectPaymentMethod();

        if (payment == null) {
            return;
        }

        /*
         * PAYMENT COMES FIRST.
         *
         * No Ticket object has been created yet.
         */
        boolean paymentSuccessful =
                paymentService.processPayment(
                        payment,
                        fare
                );

        if (!paymentSuccessful) {

            clearScreen();

            System.out.println(
                    "Payment failed."
            );

            System.out.println(
                    "No ticket was created."
            );

            waitForBack();

            return;
        }

        /*
         * Only after successful payment
         * is the Ticket created and stored.
         */
        try {

            Ticket ticket =
                    ticketService.buyTicket(
                            passenger,
                            selectedRoute,
                            ticketType
                    );

            clearScreen();

            System.out.println(
                    "========== PAYMENT SUCCESSFUL =========="
            );

            System.out.println();

            System.out.println(
                    "Payment completed successfully."
            );

            System.out.println(
                    "Your ticket has now been issued."
            );

            System.out.println();

            ticket.printTicket();

            System.out.println();

            System.out.printf(
                    "Remaining Balance: RM %.2f%n",
                    passenger.getBalance()
            );

        } catch (IllegalArgumentException e) {

            clearScreen();

            System.out.println(
                    "Ticket purchase failed: "
                            + e.getMessage()
            );
        }

        waitForBack();
    }

    /**
     * Warns Passenger when ACTIVE ticket(s)
     * already exist.
     *
     * Exact duplicate:
     * - Same Passenger
     * - Same Source
     * - Same Destination
     * - Same Ticket Type
     * - ACTIVE
     *
     * Passenger may still continue purchasing.
     */
    private static boolean confirmActiveTicketWarning(
            Passenger passenger,
            Route selectedRoute,
            TicketType selectedTicketType) {

        int activeTicketCount = 0;

        Ticket exactDuplicate =
                null;

        for (Ticket ticket : tickets) {

            if (ticket.getPassenger() == passenger
                    && ticket.getStatus()
                    == TicketStatus.ACTIVE) {

                activeTicketCount++;

                boolean sameRoute =
                        ticket.getSource()
                                == selectedRoute.getSource()
                                && ticket.getDestination()
                                == selectedRoute.getDestination();

                boolean sameType =
                        ticket.getTicketType()
                                == selectedTicketType;

                if (sameRoute
                        && sameType
                        && exactDuplicate == null) {

                    exactDuplicate =
                            ticket;
                }
            }
        }

        /*
         * No ACTIVE tickets.
         * No warning required.
         */
        if (activeTicketCount == 0) {
            return true;
        }

        clearScreen();

        System.out.println(
                "========== ACTIVE TICKET WARNING =========="
        );

        System.out.println();

        /*
         * Stronger warning for exact duplicate.
         */
        if (exactDuplicate != null) {

            System.out.println(
                    "You already have an ACTIVE ticket"
            );

            System.out.println(
                    "for the same route and ticket type."
            );

            System.out.println();

            System.out.println(
                    "Existing ticket:"
            );

            System.out.println();

            exactDuplicate.printTicket();

        } else {

            System.out.println(
                    "You currently have "
                            + activeTicketCount
                            + " ACTIVE ticket(s)."
            );

            System.out.println();

            System.out.println(
                    "Please make sure this new ticket"
            );

            System.out.println(
                    "does not conflict with your travel plans."
            );
        }

        System.out.println();

        System.out.println(
                "New ticket:"
        );

        System.out.println();

        System.out.println(
                "Route       : "
                        + selectedRoute
                        .getSource()
                        .getName()
                        + " -> "
                        + selectedRoute
                        .getDestination()
                        .getName()
        );

        System.out.println(
                "Ticket Type : "
                        + selectedTicketType
        );

        System.out.println();

        while (true) {

            System.out.print(
                    "Continue with purchase? (Y/N): "
            );

            String choice =
                    scanner.nextLine().trim();

            if (choice.equalsIgnoreCase("Y")) {
                return true;
            }

            if (choice.equalsIgnoreCase("N")) {
                return false;
            }

            System.out.println(
                    "Invalid choice. Please enter Y or N."
            );
        }
    }

    /**
     * Selects Ticket Type.
     */
    private static TicketType selectTicketType() {

        while (true) {

            clearScreen();

            System.out.println(
                    "========== TICKET TYPE =========="
            );

            System.out.println();

            System.out.println(
                    "1. Single Ticket"
            );

            System.out.println(
                    "2. Daily Ticket"
            );

            System.out.println(
                    "3. Monthly Ticket"
            );

            System.out.println();

            System.out.println(
                    "[X] Back"
            );

            System.out.println();

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    return TicketType.SINGLE;

                case "2":
                    return TicketType.DAILY;

                case "3":
                    return TicketType.MONTHLY;

                case "X":
                case "x":
                    return null;

                default:
                    showMessage(
                            "Invalid ticket type."
                    );
            }
        }
    }

    /**
     * Selects Payment implementation.
     *
     * Payment reference demonstrates polymorphism.
     */
    private static Payment selectPaymentMethod() {

        while (true) {

            clearScreen();

            System.out.println(
                    "========== PAYMENT METHOD =========="
            );

            System.out.println();

            System.out.println(
                    "1. Cash Payment"
            );

            System.out.println(
                    "2. Card Payment"
            );

            System.out.println();

            System.out.println(
                    "[X] Back"
            );

            System.out.println();

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    return new CashPayment();

                case "2":

                    System.out.print(
                            "Enter card number: "
                    );

                    String cardNumber =
                            scanner.nextLine().trim();

                    return new CardPayment(
                            cardNumber
                    );

                case "X":
                case "x":
                    return null;

                default:
                    showMessage(
                            "Invalid payment method."
                    );
            }
        }
    }

    /**
     * Finds Route by ID.
     */
    private static Route findRouteById(
            String routeId) {

        for (Route route : routes) {

            if (route.getRouteId()
                    .equalsIgnoreCase(routeId)) {

                return route;
            }
        }

        return null;
    }

    /**
     * Displays only Tickets belonging to
     * the logged-in Passenger.
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

            if (ticket.getPassenger()
                    == passenger) {

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
     * Displays Main Menu.
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
     * Displays message.
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
     * Waits for X.
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
     * Clears visible console.
     */
    private static void clearScreen() {

        System.out.print(
                "\033[H\033[2J"
        );

        System.out.flush();
    }
}