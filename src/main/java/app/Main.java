package app;

import enums.TicketStatus;
import enums.TicketType;
import enums.UserRole;
import exception.FileProcessingException;
import exception.InvalidLoginException;
import exception.TicketNotFoundException;
import fare.FareCalculator;
import fare.StandardFareCalculator;
import model.Admin;
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
import service.ReportService;
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
     * One shared TXTFileManager preserves
     * loaded object relationships.
     */
    private static final TXTFileManager fileManager =
            new TXTFileManager();

    /*
     * Shared live User collection.
     */
    private static final HashMap<String, User> users =
            new HashMap<>();

    /*
     * Shared live Route collection.
     */
    private static final ArrayList<Route> routes =
            new ArrayList<>();

    /*
     * Shared live Ticket collection.
     */
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
    private static ReportService reportService;

    public static void main(String[] args) {

        /*
         * Required startup relationship order:
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

        /*
         * Reports share the live Ticket collection.
         */
        reportService =
                new ReportService(tickets);

        /*
         * Connect loaded Admin accounts to
         * the live system services.
         */
        connectAdminServices();

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
     * Loads Users from TXT storage.
     */
    private static void loadUsers() {

        users.clear();

        try {

            Object loadedData =
                    fileManager.loadData(
                            USERS_FILE
                    );

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
     * Loads Stations from TXT storage.
     */
    private static void loadStations() {

        stationService =
                new StationService();

        try {

            Object loadedData =
                    fileManager.loadData(
                            STATIONS_FILE
                    );

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
     * Loads Trains from TXT storage.
     */
    private static void loadTrains() {

        trainService =
                new TrainService();

        try {

            Object loadedData =
                    fileManager.loadData(
                            TRAINS_FILE
                    );

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
     * Loads Routes from TXT storage.
     *
     * Main and RouteService use the same
     * live ArrayList<Route>.
     */
    private static void loadRoutes() {

        routes.clear();

        routeService =
                new RouteService(routes);

        try {

            Object loadedData =
                    fileManager.loadData(
                            ROUTES_FILE
                    );

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
     * Loads Tickets from TXT storage.
     */
    private static void loadTickets() {

        tickets.clear();

        try {

            Object loadedData =
                    fileManager.loadData(
                            TICKETS_FILE
                    );

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
     * Reconnects persisted Admin accounts
     * to the shared system services.
     */
    private static void connectAdminServices() {

        ArrayList<User> currentUsers =
                new ArrayList<>(
                        users.values()
                );

        for (User user : currentUsers) {

            if (user.getRole() == UserRole.ADMIN) {

                Admin connectedAdmin =
                        new Admin(
                                user.getUserId(),
                                user.getName(),
                                user.getEmail(),
                                user.getPassword(),
                                stationService,
                                trainService,
                                reportService
                        );

                users.put(
                        connectedAdmin.getUserId(),
                        connectedAdmin
                );
            }
        }

        userService =
                new UserService(users);
    }

    /**
     * Public registration creates
     * Passenger accounts only.
     */
    private static void registerPassenger() {

        clearScreen();

        System.out.println(
                "===== PASSENGER REGISTRATION ====="
        );

        System.out.print(
                "Passenger ID: "
        );

        String userId =
                scanner.nextLine().trim();

        System.out.print(
                "Name: "
        );

        String name =
                scanner.nextLine().trim();

        System.out.print(
                "Email: "
        );

        String email =
                scanner.nextLine().trim();

        System.out.print(
                "Password: "
        );

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
     * Handles Passenger and Admin login.
     */
    private static void login() {

        clearScreen();

        System.out.println(
                "========== LOGIN =========="
        );

        System.out.print(
                "Email: "
        );

        String email =
                scanner.nextLine().trim();

        System.out.print(
                "Password: "
        );

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

                adminMenu(
                        (Admin) user
                );
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

                case "7":
                    cancelPassengerTicket(
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
     * Admin menu.
     */
    private static void adminMenu(
            Admin admin) {

        boolean loggedIn = true;

        while (loggedIn) {

            displayAdminMenu(
                    admin
            );

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    viewAdminProfile(
                            admin
                    );
                    break;

                case "2":
                    manageStations(
                            admin
                    );
                    break;

                case "3":
                    manageTrains(
                            admin
                    );
                    break;

                case "4":
                    manageRoutes();
                    break;

                case "5":
                    manageUsers();
                    break;

                case "6":
                    showMessage(
                            "Reports will be connected next."
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
     * Displays Admin menu.
     */
    private static void displayAdminMenu(
            Admin admin) {

        clearScreen();

        System.out.println(
                "========================================"
        );

        System.out.println(
                "              ADMIN MENU"
        );

        System.out.println(
                "========================================"
        );

        System.out.println(
                "Welcome, "
                        + admin.getName()
        );

        System.out.println();

        System.out.println(
                "1. View Profile"
        );

        System.out.println(
                "2. Manage Stations"
        );

        System.out.println(
                "3. Manage Trains"
        );

        System.out.println(
                "4. Manage Routes"
        );

        System.out.println(
                "5. Manage Users"
        );

        System.out.println(
                "6. Reports"
        );

        System.out.println(
                "0. Logout"
        );

        System.out.println(
                "========================================"
        );
    }

    /**
     * Displays Admin profile.
     */
    private static void viewAdminProfile(
            Admin admin) {

        clearScreen();

        System.out.println(
                "========== ADMIN PROFILE =========="
        );

        System.out.println();

        admin.viewProfile();

        waitForBack();
    }

    /**
     * Admin Station Management menu.
     */
    private static void manageStations(
            Admin admin) {

        boolean managingStations = true;

        while (managingStations) {

            clearScreen();

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "          STATION MANAGEMENT"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "1. Add Station"
            );

            System.out.println(
                    "2. View Stations"
            );

            System.out.println(
                    "3. Search Station"
            );

            System.out.println();

            System.out.println(
                    "[X] Back"
            );

            System.out.println(
                    "========================================"
            );

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    addStationByAdmin(
                            admin
                    );
                    break;

                case "2":
                    viewStations();
                    break;

                case "3":
                    searchStationByAdmin();
                    break;

                case "X":
                case "x":
                    managingStations = false;
                    break;

                default:
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    /**
     * Allows Admin to add a Station.
     */
    private static void addStationByAdmin(
            Admin admin) {

        clearScreen();

        System.out.println(
                "========== ADD STATION =========="
        );

        System.out.println();

        System.out.println(
                "[X] Back"
        );

        System.out.println();

        System.out.print(
                "Station ID: "
        );

        String stationId =
                scanner.nextLine().trim();

        if (stationId.equalsIgnoreCase("X")) {
            return;
        }

        System.out.print(
                "Station Name: "
        );

        String name =
                scanner.nextLine().trim();

        System.out.print(
                "Location: "
        );

        String location =
                scanner.nextLine().trim();

        if (stationId.isBlank()
                || name.isBlank()
                || location.isBlank()) {

            showMessage(
                    "Station creation failed: All fields are required."
            );

            return;
        }

        if (stationId.contains("|")
                || name.contains("|")
                || location.contains("|")) {

            showMessage(
                    "Station creation failed: Character | is not allowed."
            );

            return;
        }

        Station existingStation =
                stationService.searchStation(
                        name
                );

        if (existingStation != null) {

            clearScreen();

            System.out.println(
                    "Station creation failed."
            );

            System.out.println();

            System.out.println(
                    "A station with this name already exists:"
            );

            System.out.println();

            existingStation.displayInfo();

            waitForBack();

            return;
        }

        Station station =
                new Station(
                        stationId,
                        name,
                        location
                );

        try {

            admin.addStation(
                    station
            );

            stationService.sortStationsByName();

            clearScreen();

            System.out.println(
                    "========== STATION ADDED =========="
            );

            System.out.println();

            System.out.println(
                    "Station added successfully."
            );

            System.out.println();

            station.displayInfo();

        } catch (IllegalArgumentException
                 | IllegalStateException e) {

            clearScreen();

            System.out.println(
                    "Unable to add station: "
                            + e.getMessage()
            );
        }

        waitForBack();
    }

    /**
     * Allows Admin to search Stations.
     */
    private static void searchStationByAdmin() {

        clearScreen();

        System.out.println(
                "========== SEARCH STATION =========="
        );

        System.out.println();

        System.out.println(
                "[X] Back"
        );

        System.out.println();

        System.out.print(
                "Enter station name: "
        );

        String name =
                scanner.nextLine().trim();

        if (name.equalsIgnoreCase("X")) {
            return;
        }

        if (name.isBlank()) {

            showMessage(
                    "Station name cannot be blank."
            );

            return;
        }

        Station station =
                stationService.searchStation(
                        name
                );

        clearScreen();

        if (station == null) {

            System.out.println(
                    "Station not found."
            );

        } else {

            System.out.println(
                    "========== STATION FOUND =========="
            );

            System.out.println();

            station.displayInfo();
        }

        waitForBack();
    }

    /**
     * Admin Train Management menu.
     */
    private static void manageTrains(
            Admin admin) {

        boolean managingTrains = true;

        while (managingTrains) {

            clearScreen();

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "           TRAIN MANAGEMENT"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "1. Add Train"
            );

            System.out.println(
                    "2. View Trains"
            );

            System.out.println();

            System.out.println(
                    "[X] Back"
            );

            System.out.println(
                    "========================================"
            );

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    addTrainByAdmin(
                            admin
                    );
                    break;

                case "2":
                    viewTrainsByAdmin();
                    break;

                case "X":
                case "x":
                    managingTrains = false;
                    break;

                default:
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    /**
     * Allows Admin to add a Train.
     */
    private static void addTrainByAdmin(
            Admin admin) {

        clearScreen();

        System.out.println(
                "========== ADD TRAIN =========="
        );

        System.out.println();

        System.out.println(
                "[X] Back"
        );

        System.out.println();

        System.out.print(
                "Train ID: "
        );

        String trainId =
                scanner.nextLine().trim();

        if (trainId.equalsIgnoreCase("X")) {
            return;
        }

        System.out.print(
                "Train Name: "
        );

        String trainName =
                scanner.nextLine().trim();

        System.out.print(
                "Capacity: "
        );

        String capacityInput =
                scanner.nextLine().trim();

        if (trainId.isBlank()
                || trainName.isBlank()
                || capacityInput.isBlank()) {

            showMessage(
                    "Train creation failed: All fields are required."
            );

            return;
        }

        if (trainId.contains("|")
                || trainName.contains("|")
                || capacityInput.contains("|")) {

            showMessage(
                    "Train creation failed: Character | is not allowed."
            );

            return;
        }

        int capacity;

        try {

            capacity =
                    Integer.parseInt(
                            capacityInput
                    );

        } catch (NumberFormatException e) {

            showMessage(
                    "Train creation failed: Capacity must be a whole number."
            );

            return;
        }

        if (capacity <= 0) {

            showMessage(
                    "Train creation failed: Capacity must be greater than 0."
            );

            return;
        }

        Train train =
                new Train(
                        trainId,
                        trainName,
                        capacity
                );

        try {

            admin.addTrain(
                    train
            );

            clearScreen();

            System.out.println(
                    "========== TRAIN ADDED =========="
            );

            System.out.println();

            System.out.println(
                    "Train added successfully."
            );

            System.out.println();

            train.displayTrain();

        } catch (IllegalArgumentException
                 | IllegalStateException e) {

            clearScreen();

            System.out.println(
                    "Unable to add train: "
                            + e.getMessage()
            );
        }

        waitForBack();
    }

    /**
     * Displays all Trains.
     */
    private static void viewTrainsByAdmin() {

        clearScreen();

        System.out.println(
                "========== METRO TRAINS =========="
        );

        System.out.println();

        trainService.viewTrains();

        waitForBack();
    }

    /**
     * Admin Route Management menu.
     */
    private static void manageRoutes() {

        boolean managingRoutes = true;

        while (managingRoutes) {

            clearScreen();

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "           ROUTE MANAGEMENT"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "1. Create Route"
            );

            System.out.println(
                    "2. View Routes"
            );

            System.out.println(
                    "3. Find Route"
            );

            System.out.println();

            System.out.println(
                    "[X] Back"
            );

            System.out.println(
                    "========================================"
            );

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    createRouteByAdmin();
                    break;

                case "2":
                    viewRoutes();
                    break;

                case "3":
                    findRouteByAdmin();
                    break;

                case "X":
                case "x":
                    managingRoutes = false;
                    break;

                default:
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    /**
     * Creates a Route using existing Station objects.
     */
    private static void createRouteByAdmin() {

        clearScreen();

        System.out.println(
                "========== CREATE ROUTE =========="
        );

        System.out.println();

        System.out.println(
                "Available Stations:"
        );

        System.out.println();

        stationService.viewStations();

        System.out.println();

        System.out.println(
                "[X] Back"
        );

        System.out.println();

        System.out.print(
                "Route ID: "
        );

        String routeId =
                scanner.nextLine().trim();

        if (routeId.equalsIgnoreCase("X")) {
            return;
        }

        if (routeId.isBlank()) {

            showMessage(
                    "Route creation failed: Route ID cannot be blank."
            );

            return;
        }

        if (routeId.contains("|")) {

            showMessage(
                    "Route creation failed: Character | is not allowed."
            );

            return;
        }

        System.out.print(
                "Source Station Name: "
        );

        String sourceName =
                scanner.nextLine().trim();

        if (sourceName.equalsIgnoreCase("X")) {
            return;
        }

        Station source =
                stationService.searchStation(
                        sourceName
                );

        if (source == null) {

            showMessage(
                    "Route creation failed: Source station not found."
            );

            return;
        }

        System.out.print(
                "Destination Station Name: "
        );

        String destinationName =
                scanner.nextLine().trim();

        if (destinationName.equalsIgnoreCase("X")) {
            return;
        }

        Station destination =
                stationService.searchStation(
                        destinationName
                );

        if (destination == null) {

            showMessage(
                    "Route creation failed: Destination station not found."
            );

            return;
        }

        if (source == destination) {

            showMessage(
                    "Route creation failed: Source and destination must be different."
            );

            return;
        }

        System.out.print(
                "Distance (km): "
        );

        String distanceInput =
                scanner.nextLine().trim();

        double distanceKm;

        try {

            distanceKm =
                    Double.parseDouble(
                            distanceInput
                    );

        } catch (NumberFormatException e) {

            showMessage(
                    "Route creation failed: Distance must be a valid number."
            );

            return;
        }

        if (distanceKm <= 0) {

            showMessage(
                    "Route creation failed: Distance must be greater than 0."
            );

            return;
        }

        Route route =
                new Route(
                        routeId,
                        source,
                        destination,
                        distanceKm
                );

        try {

            routeService.addRoute(
                    route
            );

            clearScreen();

            System.out.println(
                    "========== ROUTE CREATED =========="
            );

            System.out.println();

            System.out.println(
                    "Route created successfully."
            );

            System.out.println();

            route.displayRoute();

        } catch (IllegalArgumentException e) {

            clearScreen();

            System.out.println(
                    "Unable to create route: "
                            + e.getMessage()
            );
        }

        waitForBack();
    }

    /**
     * Demonstrates RouteService.findRoute().
     */
    private static void findRouteByAdmin() {

        clearScreen();

        System.out.println(
                "========== FIND ROUTE =========="
        );

        System.out.println();

        stationService.viewStations();

        System.out.println();

        System.out.println(
                "[X] Back"
        );

        System.out.println();

        System.out.print(
                "Source Station Name: "
        );

        String sourceName =
                scanner.nextLine().trim();

        if (sourceName.equalsIgnoreCase("X")) {
            return;
        }

        Station source =
                stationService.searchStation(
                        sourceName
                );

        if (source == null) {

            showMessage(
                    "Source station not found."
            );

            return;
        }

        System.out.print(
                "Destination Station Name: "
        );

        String destinationName =
                scanner.nextLine().trim();

        if (destinationName.equalsIgnoreCase("X")) {
            return;
        }

        Station destination =
                stationService.searchStation(
                        destinationName
                );

        if (destination == null) {

            showMessage(
                    "Destination station not found."
            );

            return;
        }

        Route route =
                routeService.findRoute(
                        source,
                        destination
                );

        clearScreen();

        if (route == null) {

            System.out.println(
                    "Route not found."
            );

        } else {

            System.out.println(
                    "========== ROUTE FOUND =========="
            );

            System.out.println();

            route.displayRoute();
        }

        waitForBack();
    }

    /**
     * Admin User Management menu.
     *
     * Uses lecturer-required UserService
     * rather than adding user management
     * methods into Admin.
     */
    private static void manageUsers() {

        boolean managingUsers = true;

        while (managingUsers) {

            clearScreen();

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "            USER MANAGEMENT"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "1. View Users"
            );

            System.out.println(
                    "2. Add Passenger"
            );

            System.out.println(
                    "3. Add Admin"
            );

            System.out.println();

            System.out.println(
                    "[X] Back"
            );

            System.out.println(
                    "========================================"
            );

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    viewUsersByAdmin();
                    break;

                case "2":
                    addPassengerByAdmin();
                    break;

                case "3":
                    addAdminByAdmin();
                    break;

                case "X":
                case "x":
                    managingUsers = false;
                    break;

                default:
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    /**
     * Displays all registered Users.
     */
    private static void viewUsersByAdmin() {

        clearScreen();

        userService.viewAllUsers();

        waitForBack();
    }

    /**
     * Allows an authenticated Admin
     * to create a Passenger account.
     */
    private static void addPassengerByAdmin() {

        clearScreen();

        System.out.println(
                "========== ADD PASSENGER =========="
        );

        System.out.println();

        System.out.println(
                "[X] Back"
        );

        System.out.println();

        System.out.print(
                "Passenger ID: "
        );

        String userId =
                scanner.nextLine().trim();

        if (userId.equalsIgnoreCase("X")) {
            return;
        }

        System.out.print(
                "Name: "
        );

        String name =
                scanner.nextLine().trim();

        System.out.print(
                "Email: "
        );

        String email =
                scanner.nextLine().trim();

        System.out.print(
                "Password: "
        );

        String password =
                scanner.nextLine();

        if (userId.isBlank()
                || name.isBlank()
                || email.isBlank()
                || password.isBlank()) {

            showMessage(
                    "Registration failed: All fields are required."
            );

            return;
        }

        Passenger passenger =
                new Passenger(
                        userId,
                        name,
                        email,
                        password
                );

        clearScreen();

        userService.registerUser(
                passenger
        );

        waitForBack();
    }

    /**
     * Allows an authenticated Admin
     * to create another Admin account.
     *
     * The new Admin is immediately connected
     * to the live Station, Train and Report services.
     */
    private static void addAdminByAdmin() {

        clearScreen();

        System.out.println(
                "========== ADD ADMIN =========="
        );

        System.out.println();

        System.out.println(
                "[X] Back"
        );

        System.out.println();

        System.out.print(
                "Admin ID: "
        );

        String userId =
                scanner.nextLine().trim();

        if (userId.equalsIgnoreCase("X")) {
            return;
        }

        System.out.print(
                "Name: "
        );

        String name =
                scanner.nextLine().trim();

        System.out.print(
                "Email: "
        );

        String email =
                scanner.nextLine().trim();

        System.out.print(
                "Password: "
        );

        String password =
                scanner.nextLine();

        if (userId.isBlank()
                || name.isBlank()
                || email.isBlank()
                || password.isBlank()) {

            showMessage(
                    "Registration failed: All fields are required."
            );

            return;
        }

        /*
         * Create a fully connected Admin so
         * the account works immediately without restart.
         */
        Admin newAdmin =
                new Admin(
                        userId,
                        name,
                        email,
                        password,
                        stationService,
                        trainService,
                        reportService
                );

        clearScreen();

        userService.registerUser(
                newAdmin
        );

        waitForBack();
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
                "7. Cancel Ticket"
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
                    Double.parseDouble(
                            input
                    );

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
     * Passenger Ticket purchasing workflow.
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
                        + selectedRoute.getSource().getName()
                        + " -> "
                        + selectedRoute.getDestination().getName()
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
     * Warns Passenger when ACTIVE Ticket(s)
     * already exist.
     */
    private static boolean confirmActiveTicketWarning(
            Passenger passenger,
            Route selectedRoute,
            TicketType selectedTicketType) {

        int activeTicketCount = 0;

        Ticket exactDuplicate = null;

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

        if (activeTicketCount == 0) {
            return true;
        }

        clearScreen();

        System.out.println(
                "========== ACTIVE TICKET WARNING =========="
        );

        System.out.println();

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
                        + selectedRoute.getSource().getName()
                        + " -> "
                        + selectedRoute.getDestination().getName()
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
     * Passenger cancellation workflow.
     */
    private static void cancelPassengerTicket(
            Passenger passenger) {

        clearScreen();

        System.out.println(
                "========== CANCEL TICKET =========="
        );

        System.out.println();

        boolean activeTicketFound = false;

        for (Ticket ticket : tickets) {

            if (ticket.getPassenger() == passenger
                    && ticket.getStatus()
                    == TicketStatus.ACTIVE) {

                ticket.printTicket();

                System.out.println(
                        "-------------------------"
                );

                activeTicketFound = true;
            }
        }

        if (!activeTicketFound) {

            System.out.println(
                    "You have no ACTIVE tickets to cancel."
            );

            waitForBack();

            return;
        }

        System.out.println();

        System.out.print(
                "Enter Ticket ID or X to go back: "
        );

        String ticketId =
                scanner.nextLine().trim();

        if (ticketId.equalsIgnoreCase("X")) {
            return;
        }

        Ticket selectedTicket =
                findPassengerTicketById(
                        passenger,
                        ticketId
                );

        if (selectedTicket == null) {

            showMessage(
                    "Ticket not found for this passenger."
            );

            return;
        }

        if (selectedTicket.getStatus()
                != TicketStatus.ACTIVE) {

            clearScreen();

            System.out.println(
                    "This ticket cannot be cancelled."
            );

            System.out.println(
                    "Current status: "
                            + selectedTicket.getStatus()
            );

            waitForBack();

            return;
        }

        clearScreen();

        System.out.println(
                "========== CANCELLATION SUMMARY =========="
        );

        System.out.println();

        selectedTicket.printTicket();

        System.out.println();

        while (true) {

            System.out.print(
                    "Confirm cancellation? (Y/N): "
            );

            String confirmation =
                    scanner.nextLine().trim();

            if (confirmation.equalsIgnoreCase("N")) {

                showMessage(
                        "Cancellation cancelled."
                );

                return;
            }

            if (confirmation.equalsIgnoreCase("Y")) {
                break;
            }

            System.out.println(
                    "Invalid choice. Please enter Y or N."
            );
        }

        try {

            ticketService.cancelTicket(
                    selectedTicket.getTicketId()
            );

            clearScreen();

            System.out.println(
                    "========== TICKET CANCELLED =========="
            );

            System.out.println();

            System.out.println(
                    "Ticket cancelled successfully."
            );

            System.out.println();

            selectedTicket.printTicket();

            System.out.println();

            System.out.println(
                    "The ticket remains stored for history and reporting."
            );

        } catch (TicketNotFoundException e) {

            clearScreen();

            System.out.println(
                    "Cancellation failed: "
                            + e.getMessage()
            );
        }

        waitForBack();
    }

    /**
     * Finds a Ticket belonging to
     * the logged-in Passenger.
     */
    private static Ticket findPassengerTicketById(
            Passenger passenger,
            String ticketId) {

        for (Ticket ticket : tickets) {

            if (ticket.getPassenger() == passenger
                    && ticket.getTicketId()
                    .equalsIgnoreCase(ticketId)) {

                return ticket;
            }
        }

        return null;
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
     * Displays Tickets belonging to
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
     * Displays a message.
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
     * Clears the visible console.
     */
    private static void clearScreen() {

        System.out.print(
                "\033[H\033[2J"
        );

        System.out.flush();
    }
}