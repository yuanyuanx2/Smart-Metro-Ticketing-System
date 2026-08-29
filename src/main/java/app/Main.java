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
import report.PDFReportExporter;
import repository.JSONFileManager;
import repository.TXTFileManager;
import service.JSONBackupService;
import service.LoyaltyDiscountService;
import service.PaymentService;
import service.ReportService;
import service.RouteService;
import service.StationService;
import service.TicketService;
import service.TrainService;
import service.TravelHistoryService;
import service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
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

    private static final String REPORT_DIRECTORY =
            "src/main/resources/data/";

    private static final DateTimeFormatter REPORT_FILE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "yyyyMMdd_HHmmss"
            );

    /*
     * Lecturer-required TXT persistence.
     */
    private static final TXTFileManager fileManager =
            new TXTFileManager();

    /*
     * PDF report exporter.
     */
    private static final PDFReportExporter pdfReportExporter =
            new PDFReportExporter();

    /*
     * Shared live collections.
     */
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

    /*
     * Bonus loyalty-discount service.
     *
     * Uses the same live Ticket collection so
     * reward eligibility is based on the
     * Passenger's current Ticket history.
     */
    private static final LoyaltyDiscountService loyaltyDiscountService =
            new LoyaltyDiscountService(
                    tickets
            );

    /*
     * Bonus passenger travel-history service.
     *
     * Uses the same live Ticket collection so
     * history automatically reflects loaded,
     * purchased, and cancelled Tickets.
     */
    private static final TravelHistoryService travelHistoryService =
            new TravelHistoryService(
                    tickets
            );

    private static UserService userService;
    private static StationService stationService;
    private static TrainService trainService;
    private static RouteService routeService;
    private static TicketService ticketService;
    private static ReportService reportService;

    public static void main(String[] args) {

        /*
         * Required relationship loading order:
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

        reportService =
                new ReportService(
                        tickets
                );

        connectAdminServices();

        boolean running =
                true;

        while (running) {

            displayMainMenu();

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine()
                            .trim();

            switch (choice) {

                case "1":
                    login();
                    break;

                case "2":
                    registerPassenger();
                    break;

                case "0":
                    running =
                            false;
                    break;

                default:
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }

        clearScreen();

        saveImportantData();

        System.out.println();

        System.out.println(
                "Thank you for using Smart Metro Ticketing System."
        );

        scanner.close();
    }

    /**
     * Saves all important system data
     * before the program exits.
     */
    private static void saveImportantData() {

        boolean usersSaved =
                saveDataSafely(
                        users,
                        USERS_FILE,
                        "users"
                );

        boolean stationsSaved =
                saveDataSafely(
                        stationService.getStations(),
                        STATIONS_FILE,
                        "stations"
                );

        boolean trainsSaved =
                saveDataSafely(
                        trainService.getTrains(),
                        TRAINS_FILE,
                        "trains"
                );

        boolean routesSaved =
                saveDataSafely(
                        routes,
                        ROUTES_FILE,
                        "routes"
                );

        boolean ticketsSaved =
                saveDataSafely(
                        tickets,
                        TICKETS_FILE,
                        "tickets"
                );

        System.out.println();

        if (usersSaved
                && stationsSaved
                && trainsSaved
                && routesSaved
                && ticketsSaved) {

            System.out.println(
                    "All important data saved successfully."
            );

        } else {

            System.out.println(
                    "Warning: Some data could not be saved."
            );
        }
    }

    /**
     * Safely saves one group of TXT data.
     */
    private static boolean saveDataSafely(
            Object data,
            String fileName,
            String dataName) {

        try {

            fileManager.saveData(
                    data,
                    fileName
            );

            return true;

        } catch (FileProcessingException e) {

            System.out.println(
                    "Unable to save "
                            + dataName
                            + ": "
                            + e.getMessage()
            );

            return false;
        }
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

            if (loadedData
                    instanceof ArrayList<?> loadedUsers) {

                for (Object item :
                        loadedUsers) {

                    if (item
                            instanceof User user) {

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
                new UserService(
                        users
                );
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

            if (loadedData
                    instanceof ArrayList<?> loadedStations) {

                for (Object item :
                        loadedStations) {

                    if (item
                            instanceof Station station) {

                        stationService.addStation(
                                station
                        );
                    }
                }
            }

            stationService.sortStationsById();

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

            if (loadedData
                    instanceof ArrayList<?> loadedTrains) {

                for (Object item :
                        loadedTrains) {

                    if (item
                            instanceof Train train) {

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
     */
    private static void loadRoutes() {

        routes.clear();

        routeService =
                new RouteService(
                        routes
                );

        try {

            Object loadedData =
                    fileManager.loadData(
                            ROUTES_FILE
                    );

            if (loadedData
                    instanceof ArrayList<?> loadedRoutes) {

                for (Object item :
                        loadedRoutes) {

                    if (item
                            instanceof Route route) {

                        routeService.addRoute(
                                route
                        );
                    }
                }

                routeService.sortRoutesById();
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

            if (loadedData
                    instanceof ArrayList<?> loadedTickets) {

                for (Object item :
                        loadedTickets) {

                    if (item
                            instanceof Ticket ticket) {

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
     * Reconnects loaded Admin accounts
     * to the live service objects.
     */
    private static void connectAdminServices() {

        ArrayList<User> currentUsers =
                new ArrayList<>(
                        users.values()
                );

        for (User user :
                currentUsers) {

            if (user.getRole()
                    == UserRole.ADMIN) {

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
                new UserService(
                        users
                );
    }

    /**
     * Passenger registration.
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
                scanner.nextLine()
                        .trim()
                        .toUpperCase(Locale.ROOT);

        System.out.print(
                "Name: "
        );

        String name =
                scanner.nextLine()
                        .trim();

        System.out.print(
                "Email: "
        );

        String email =
                scanner.nextLine()
                        .trim();

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
     * Handles Passenger/Admin login.
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
                scanner.nextLine()
                        .trim();

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

        boolean loggedIn =
                true;

        while (loggedIn) {

            displayPassengerMenu(
                    passenger
            );

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine()
                            .trim();

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
                    managePassengerTravelHistory(
                            passenger
                    );
                    break;

                case "8":
                    cancelPassengerTicket(
                            passenger
                    );
                    break;

                case "0":
                    loggedIn =
                            false;
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

        boolean loggedIn =
                true;

        while (loggedIn) {

            displayAdminMenu(
                    admin
            );

            System.out.print(
                    "Enter choice: "
            );

            String choice =
                    scanner.nextLine()
                            .trim();

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
                    manageReports(
                            admin
                    );
                    break;

                case "7":
                    manageDataBackup();
                    break;

                case "0":
                    loggedIn =
                            false;
                    break;

                default:
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    /**
     * JSON backup menu.
     */
    private static void manageDataBackup() {

        boolean managingBackup =
                true;

        while (managingBackup) {

            clearScreen();

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "             DATA BACKUP"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "Primary Storage: TXT"
            );

            System.out.println(
                    "Backup Format  : JSON"
            );

            System.out.println();

            System.out.println(
                    "1. Create JSON Backup"
            );

            System.out.println(
                    "2. Verify JSON Backup"
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
                    scanner.nextLine()
                            .trim();

            switch (choice) {

                case "1":
                    createJsonBackup();
                    break;

                case "2":
                    verifyJsonBackup();
                    break;

                case "X":
                case "x":
                    managingBackup =
                            false;
                    break;

                default:
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    /**
     * Creates JSON backup.
     */
    private static void createJsonBackup() {

        clearScreen();

        JSONBackupService backupService =
                new JSONBackupService(
                        new JSONFileManager()
                );

        try {

            backupService.createBackup(
                    users,
                    stationService.getStations(),
                    trainService.getTrains(),
                    routes,
                    tickets
            );

            System.out.println(
                    "JSON backup created successfully."
            );

            System.out.println();

            System.out.println(
                    "Backup location:"
            );

            System.out.println(
                    backupService.getBackupDirectory()
            );

            System.out.println();

            System.out.println(
                    "Files created:"
            );

            System.out.println(
                    "users.json"
            );

            System.out.println(
                    "stations.json"
            );

            System.out.println(
                    "trains.json"
            );

            System.out.println(
                    "routes.json"
            );

            System.out.println(
                    "tickets.json"
            );

        } catch (FileProcessingException e) {

            System.out.println(
                    "JSON backup failed: "
                            + e.getMessage()
            );
        }

        waitForBack();
    }

    /**
     * Verifies JSON backup by loading
     * the saved data again.
     */
    private static void verifyJsonBackup() {

        clearScreen();

        JSONBackupService backupService =
                new JSONBackupService(
                        new JSONFileManager()
                );

        try {

            boolean valid =
                    backupService.verifyBackup(
                            users.size(),
                            stationService
                                    .getStations()
                                    .size(),
                            trainService
                                    .getTrains()
                                    .size(),
                            routes.size(),
                            tickets.size()
                    );

            if (valid) {

                System.out.println(
                        "JSON backup verification PASSED."
                );

                System.out.println();

                System.out.println(
                        "The backup matches the current system data."
                );

                System.out.println(
                        "Users, stations, trains, routes and tickets"
                );

                System.out.println(
                        "were loaded successfully from JSON."
                );

            } else {

                System.out.println(
                        "JSON backup verification FAILED."
                );

                System.out.println();

                System.out.println(
                        "The JSON backup does not match"
                );

                System.out.println(
                        "the current live system record counts."
                );
            }

        } catch (FileProcessingException e) {

            System.out.println(
                    "Unable to verify JSON backup: "
                            + e.getMessage()
            );
        }

        waitForBack();
    }

    /**
     * Admin reporting menu.
     */
    private static void manageReports(
            Admin admin) {

        boolean viewingReports =
                true;

        while (viewingReports) {

            clearScreen();

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "              REPORTS"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "1. Summary Report"
            );

            System.out.println(
                    "2. Ticket Type Statistics"
            );

            System.out.println(
                    "3. Route Popularity"
            );

            System.out.println(
                    "4. Fare Statistics"
            );

            System.out.println(
                    "5. Passenger Financial Summary"
            );

            System.out.println(
                    "6. Monthly Report"
            );

            System.out.println(
                    "7. Quarterly Report"
            );

            System.out.println(
                    "8. Yearly Report"
            );

            System.out.println(
                    "9. Export Reports"
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
                    scanner.nextLine()
                            .trim();

            switch (choice) {

                case "1":
                    viewReportsByAdmin(
                            admin
                    );
                    break;

                case "2":
                    viewTicketTypeStatistics();
                    break;

                case "3":
                    viewRoutePopularity();
                    break;

                case "4":
                    viewFareStatistics();
                    break;

                case "5":
                    viewPassengerFinancialSummary();
                    break;

                case "6":
                    viewMonthlyReport();
                    break;

                case "7":
                    viewQuarterlyReport();
                    break;

                case "8":
                    viewYearlyReport();
                    break;

                case "9":
                    manageReportExports();
                    break;

                case "X":
                case "x":
                    viewingReports =
                            false;
                    break;

                default:
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    /**
     * Report export menu.
     */
    private static void manageReportExports() {

        boolean exporting =
                true;

        while (exporting) {

            clearScreen();

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "           EXPORT REPORTS"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "1. Export All-Time Report"
            );

            System.out.println(
                    "2. Export Monthly Report"
            );

            System.out.println(
                    "3. Export Quarterly Report"
            );

            System.out.println(
                    "4. Export Yearly Report"
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
                    scanner.nextLine()
                            .trim();

            switch (choice) {

                case "1":
                    exportAllTimeReport();
                    break;

                case "2":
                    exportMonthlyReport();
                    break;

                case "3":
                    exportQuarterlyReport();
                    break;

                case "4":
                    exportYearlyReport();
                    break;

                case "X":
                case "x":
                    exporting =
                            false;
                    break;

                default:
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    /**
     * Lecturer-required summary report.
     */
    private static void viewReportsByAdmin(
            Admin admin) {

        clearScreen();

        System.out.println(
                "========== SUMMARY REPORT =========="
        );

        System.out.println();

        try {

            admin.viewReports();

        } catch (IllegalStateException e) {

            System.out.println(
                    "Unable to display reports: "
                            + e.getMessage()
            );
        }

        waitForBack();
    }

    /**
     * Ticket type statistics.
     */
    private static void viewTicketTypeStatistics() {

        clearScreen();

        reportService.showTicketTypeStatistics();

        waitForBack();
    }

    /**
     * Route popularity.
     */
    private static void viewRoutePopularity() {

        clearScreen();

        reportService.showRoutePopularity();

        waitForBack();
    }

    /**
     * Fare statistics.
     */
    private static void viewFareStatistics() {

        clearScreen();

        reportService.showFareStatistics();

        waitForBack();
    }

    /**
     * Bonus all-time Passenger financial summary.
     */
    private static void viewPassengerFinancialSummary() {

        clearScreen();

        reportService.showPassengerFinancialSummary(
                users
        );

        waitForBack();
    }

    /**
     * Monthly report.
     */
    private static void viewMonthlyReport() {

        clearScreen();

        System.out.println(
                "========== MONTHLY REPORT =========="
        );

        System.out.println();

        System.out.println(
                "[X] Back"
        );

        System.out.println();

        Integer year =
                readYear(
                        "Enter year: "
                );

        if (year == null) {
            return;
        }

        Integer month =
                readMonth(
                        "Enter month (1-12): "
                );

        if (month == null) {
            return;
        }

        clearScreen();

        reportService.generateMonthlyReport(
                year,
                month
        );

        waitForBack();
    }

    /**
     * Quarterly report.
     */
    private static void viewQuarterlyReport() {

        clearScreen();

        System.out.println(
                "========== QUARTERLY REPORT =========="
        );

        System.out.println();

        System.out.println(
                "Q1 = January - March"
        );

        System.out.println(
                "Q2 = April - June"
        );

        System.out.println(
                "Q3 = July - September"
        );

        System.out.println(
                "Q4 = October - December"
        );

        System.out.println();

        System.out.println(
                "[X] Back"
        );

        System.out.println();

        Integer year =
                readYear(
                        "Enter year: "
                );

        if (year == null) {
            return;
        }

        Integer quarter =
                readQuarter(
                        "Enter quarter (1-4): "
                );

        if (quarter == null) {
            return;
        }

        clearScreen();

        reportService.generateQuarterlyReport(
                year,
                quarter
        );

        waitForBack();
    }

    /**
     * Yearly report.
     */
    private static void viewYearlyReport() {

        clearScreen();

        System.out.println(
                "========== YEARLY REPORT =========="
        );

        System.out.println();

        System.out.println(
                "[X] Back"
        );

        System.out.println();

        Integer year =
                readYear(
                        "Enter year: "
                );

        if (year == null) {
            return;
        }

        clearScreen();

        reportService.generateYearlyReport(
                year
        );

        waitForBack();
    }

    /**
     * Exports all-time report.
     */
    private static void exportAllTimeReport() {

        ArrayList<String> report =
                reportService.buildExportReport(
                        users
                );

        String format =
                selectExportFormat();

        if (format == null) {
            return;
        }

        String timestamp =
                createReportTimestamp();

        if (format.equals("TXT")) {

            exportReportToTxt(
                    report,
                    "system_report_all_time_"
                            + timestamp
                            + ".txt"
            );

        } else {

            exportReportToPdf(
                    report,
                    "Smart Metro Ticketing System - All-Time Report",
                    "system_report_all_time_"
                            + timestamp
                            + ".pdf"
            );
        }
    }

    /**
     * Exports monthly report.
     */
    private static void exportMonthlyReport() {

        clearScreen();

        System.out.println(
                "========== EXPORT MONTHLY REPORT =========="
        );

        System.out.println();

        System.out.println(
                "[X] Back"
        );

        System.out.println();

        Integer year =
                readYear(
                        "Enter year: "
                );

        if (year == null) {
            return;
        }

        Integer month =
                readMonth(
                        "Enter month (1-12): "
                );

        if (month == null) {
            return;
        }

        ArrayList<String> report =
                reportService.buildMonthlyExportReport(
                        year,
                        month
                );

        String format =
                selectExportFormat();

        if (format == null) {
            return;
        }

        String timestamp =
                createReportTimestamp();

        if (format.equals("TXT")) {

            String fileName =
                    String.format(
                            "system_report_monthly_%04d_%02d_%s.txt",
                            year,
                            month,
                            timestamp
                    );

            exportReportToTxt(
                    report,
                    fileName
            );

        } else {

            String fileName =
                    String.format(
                            "system_report_monthly_%04d_%02d_%s.pdf",
                            year,
                            month,
                            timestamp
                    );

            String title =
                    String.format(
                            "Smart Metro Ticketing System - Monthly Report %04d-%02d",
                            year,
                            month
                    );

            exportReportToPdf(
                    report,
                    title,
                    fileName
            );
        }
    }

    /**
     * Exports quarterly report.
     */
    private static void exportQuarterlyReport() {

        clearScreen();

        System.out.println(
                "========== EXPORT QUARTERLY REPORT =========="
        );

        System.out.println();

        System.out.println(
                "Q1 = January - March"
        );

        System.out.println(
                "Q2 = April - June"
        );

        System.out.println(
                "Q3 = July - September"
        );

        System.out.println(
                "Q4 = October - December"
        );

        System.out.println();

        System.out.println(
                "[X] Back"
        );

        System.out.println();

        Integer year =
                readYear(
                        "Enter year: "
                );

        if (year == null) {
            return;
        }

        Integer quarter =
                readQuarter(
                        "Enter quarter (1-4): "
                );

        if (quarter == null) {
            return;
        }

        ArrayList<String> report =
                reportService.buildQuarterlyExportReport(
                        year,
                        quarter
                );

        String format =
                selectExportFormat();

        if (format == null) {
            return;
        }

        String timestamp =
                createReportTimestamp();

        if (format.equals("TXT")) {

            String fileName =
                    String.format(
                            "system_report_quarterly_%04d_Q%d_%s.txt",
                            year,
                            quarter,
                            timestamp
                    );

            exportReportToTxt(
                    report,
                    fileName
            );

        } else {

            String fileName =
                    String.format(
                            "system_report_quarterly_%04d_Q%d_%s.pdf",
                            year,
                            quarter,
                            timestamp
                    );

            String title =
                    String.format(
                            "Smart Metro Ticketing System - %04d Q%d Report",
                            year,
                            quarter
                    );

            exportReportToPdf(
                    report,
                    title,
                    fileName
            );
        }
    }

    /**
     * Exports yearly report.
     */
    private static void exportYearlyReport() {

        clearScreen();

        System.out.println(
                "========== EXPORT YEARLY REPORT =========="
        );

        System.out.println();

        System.out.println(
                "[X] Back"
        );

        System.out.println();

        Integer year =
                readYear(
                        "Enter year: "
                );

        if (year == null) {
            return;
        }

        ArrayList<String> report =
                reportService.buildYearlyExportReport(
                        year
                );

        String format =
                selectExportFormat();

        if (format == null) {
            return;
        }

        String timestamp =
                createReportTimestamp();

        if (format.equals("TXT")) {

            String fileName =
                    String.format(
                            "system_report_yearly_%04d_%s.txt",
                            year,
                            timestamp
                    );

            exportReportToTxt(
                    report,
                    fileName
            );

        } else {

            String fileName =
                    String.format(
                            "system_report_yearly_%04d_%s.pdf",
                            year,
                            timestamp
                    );

            String title =
                    String.format(
                            "Smart Metro Ticketing System - %04d Yearly Report",
                            year
                    );

            exportReportToPdf(
                    report,
                    title,
                    fileName
            );
        }
    }

    /**
     * Allows Admin to select export format.
     */
    private static String selectExportFormat() {

        while (true) {

            clearScreen();

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "            EXPORT FORMAT"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "1. TXT"
            );

            System.out.println(
                    "2. PDF"
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
                    scanner.nextLine()
                            .trim();

            switch (choice) {

                case "1":
                    return "TXT";

                case "2":
                    return "PDF";

                case "X":
                case "x":
                    return null;

                default:
                    showMessage(
                            "Invalid export format."
                    );
            }
        }
    }

    /**
     * Exports report using TXTFileManager.
     */
    private static void exportReportToTxt(
            ArrayList<String> report,
            String fileName) {

        clearScreen();

        String reportFile =
                REPORT_DIRECTORY
                        + fileName;

        boolean saved =
                saveDataSafely(
                        report,
                        reportFile,
                        "report"
                );

        if (saved) {

            System.out.println(
                    "TXT report exported successfully."
            );

            System.out.println();

            System.out.println(
                    "File:"
            );

            System.out.println(
                    reportFile
            );
        }

        waitForBack();
    }

    /**
     * Exports report as PDF.
     */
    private static void exportReportToPdf(
            ArrayList<String> report,
            String reportTitle,
            String fileName) {

        clearScreen();

        String reportFile =
                REPORT_DIRECTORY
                        + fileName;

        try {

            pdfReportExporter.exportReport(
                    report,
                    reportTitle,
                    reportFile
            );

            System.out.println(
                    "PDF report exported successfully."
            );

            System.out.println();

            System.out.println(
                    "File:"
            );

            System.out.println(
                    reportFile
            );

        } catch (FileProcessingException e) {

            System.out.println(
                    "Unable to export PDF report: "
                            + e.getMessage()
            );
        }

        waitForBack();
    }

    /**
     * Creates unique report file timestamp.
     */
    private static String createReportTimestamp() {

        return LocalDateTime.now()
                .format(
                        REPORT_FILE_FORMAT
                );
    }

    /**
     * Reads year.
     */
    private static Integer readYear(
            String prompt) {

        System.out.print(
                prompt
        );

        String input =
                scanner.nextLine()
                        .trim();

        if (input.equalsIgnoreCase("X")) {
            return null;
        }

        try {

            int year =
                    Integer.parseInt(
                            input
                    );

            if (year < 1
                    || year > 9999) {

                showMessage(
                        "Invalid year."
                );

                return null;
            }

            return year;

        } catch (NumberFormatException e) {

            showMessage(
                    "Invalid year. Please enter a whole number."
            );

            return null;
        }
    }

    /**
     * Reads month.
     */
    private static Integer readMonth(
            String prompt) {

        System.out.print(
                prompt
        );

        String input =
                scanner.nextLine()
                        .trim();

        if (input.equalsIgnoreCase("X")) {
            return null;
        }

        try {

            int month =
                    Integer.parseInt(
                            input
                    );

            if (month < 1
                    || month > 12) {

                showMessage(
                        "Invalid month. Please enter a value from 1 to 12."
                );

                return null;
            }

            return month;

        } catch (NumberFormatException e) {

            showMessage(
                    "Invalid month. Please enter a whole number."
            );

            return null;
        }
    }

    /**
     * Reads quarter.
     */
    private static Integer readQuarter(
            String prompt) {

        System.out.print(
                prompt
        );

        String input =
                scanner.nextLine()
                        .trim();

        if (input.equalsIgnoreCase("X")) {
            return null;
        }

        try {

            int quarter =
                    Integer.parseInt(
                            input
                    );

            if (quarter < 1
                    || quarter > 4) {

                showMessage(
                        "Invalid quarter. Please enter a value from 1 to 4."
                );

                return null;
            }

            return quarter;

        } catch (NumberFormatException e) {

            showMessage(
                    "Invalid quarter. Please enter a whole number."
            );

            return null;
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
                "7. Data Backup"
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
     * Station Management menu.
     */
    private static void manageStations(
            Admin admin) {

        boolean managingStations =
                true;

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
                    scanner.nextLine()
                            .trim();

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
                    managingStations =
                            false;
                    break;

                default:
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    /**
     * Adds Station.
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
                scanner.nextLine()
                        .trim()
                        .toUpperCase(Locale.ROOT);

        if (stationId.equalsIgnoreCase("X")) {
            return;
        }

        System.out.print(
                "Station Name: "
        );

        String name =
                scanner.nextLine()
                        .trim();

        System.out.print(
                "Location: "
        );

        String location =
                scanner.nextLine()
                        .trim();

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

            stationService.sortStationsById();

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
     * Searches Station.
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
                scanner.nextLine()
                        .trim();

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
     * Train Management menu.
     */
    private static void manageTrains(
            Admin admin) {

        boolean managingTrains =
                true;

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
                    scanner.nextLine()
                            .trim();

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
                    managingTrains =
                            false;
                    break;

                default:
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    /**
     * Adds Train.
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
                scanner.nextLine()
                        .trim()
                        .toUpperCase(Locale.ROOT);

        if (trainId.equalsIgnoreCase("X")) {
            return;
        }

        System.out.print(
                "Train Name: "
        );

        String trainName =
                scanner.nextLine()
                        .trim();

        System.out.print(
                "Capacity: "
        );

        String capacityInput =
                scanner.nextLine()
                        .trim();

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
     * Displays Trains.
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
     * Route Management menu.
     */
    private static void manageRoutes() {

        boolean managingRoutes =
                true;

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
                    scanner.nextLine()
                            .trim();

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
                    managingRoutes =
                            false;
                    break;

                default:
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    /**
     * Creates Route.
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
                scanner.nextLine()
                        .trim()
                        .toUpperCase(Locale.ROOT);

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
                scanner.nextLine()
                        .trim();

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
                scanner.nextLine()
                        .trim();

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
                scanner.nextLine()
                        .trim();

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

            routeService.sortRoutesById();

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
     * Finds Route.
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
                scanner.nextLine()
                        .trim();

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
                scanner.nextLine()
                        .trim();

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
     * User Management menu.
     */
    private static void manageUsers() {

        boolean managingUsers =
                true;

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
                    scanner.nextLine()
                            .trim();

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
                    managingUsers =
                            false;
                    break;

                default:
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    /**
     * Displays Users.
     */
    private static void viewUsersByAdmin() {

        clearScreen();

        userService.viewAllUsers();

        waitForBack();
    }

    /**
     * Admin creates Passenger.
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
                scanner.nextLine()
                        .trim();

        if (userId.equalsIgnoreCase("X")) {
            return;
        }

        System.out.print(
                "Name: "
        );

        String name =
                scanner.nextLine()
                        .trim();

        System.out.print(
                "Email: "
        );

        String email =
                scanner.nextLine()
                        .trim();

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
     * Admin creates another Admin.
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
                scanner.nextLine()
                        .trim();

        if (userId.equalsIgnoreCase("X")) {
            return;
        }

        System.out.print(
                "Name: "
        );

        String name =
                scanner.nextLine()
                        .trim();

        System.out.print(
                "Email: "
        );

        String email =
                scanner.nextLine()
                        .trim();

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
     * Passenger menu display.
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
                "7. Travel History"
        );

        System.out.println(
                "8. Cancel Ticket"
        );

        System.out.println(
                "0. Logout"
        );

        System.out.println(
                "========================================"
        );
    }

    /**
     * Passenger profile.
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
     * Passenger top-up.
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
                scanner.nextLine()
                        .trim();

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
     *
     * Route and Ticket Type are selected first.
     * Standard fare is calculated.
     * A one-time loyalty reward is applied
     * when the Passenger is eligible.
     * Payment is processed using the final fare.
     * Only after successful payment is
     * the Ticket issued and stored.
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
                scanner.nextLine()
                        .trim();

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

        /*
         * Lecturer-required standard fare
         * calculation remains unchanged.
         */
        double standardFare =
                fareCalculator.calculateFare(
                        selectedRoute,
                        ticketType
                );

        /*
         * Bonus loyalty-discount calculation.
         */
        boolean loyaltyDiscountApplied =
                loyaltyDiscountService
                        .isDiscountAvailable(
                                passenger
                        );

        double discountAmount =
                loyaltyDiscountService
                        .calculateDiscountAmount(
                                passenger,
                                standardFare
                        );

        double finalFare =
                loyaltyDiscountService
                        .calculateFinalFare(
                                passenger,
                                standardFare
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
                "Route          : "
                        + selectedRoute
                        .getSource()
                        .getName()
                        + " -> "
                        + selectedRoute
                        .getDestination()
                        .getName()
        );

        System.out.println(
                "Ticket Type    : "
                        + ticketType
        );

        System.out.printf(
                "Standard Fare  : RM %.2f%n",
                standardFare
        );

        System.out.println();

        if (loyaltyDiscountApplied) {

            System.out.println(
                    "Loyalty Status : REWARD AVAILABLE"
            );

            System.out.println(
                    "Reward         : One-time 20% discount"
            );

            System.out.printf(
                    "Discount       : -RM %.2f%n",
                    discountAmount
            );

        } else {

            double progress =
                    loyaltyDiscountService
                            .getProgressTowardNextReward(
                                    passenger
                            );

            System.out.println(
                    "Loyalty Status : Not yet eligible"
            );

            System.out.printf(
                    "Loyalty Progress: RM %.2f / RM 100.00%n",
                    progress
            );
        }

        System.out.println();

        System.out.printf(
                "Final Fare     : RM %.2f%n",
                finalFare
        );

        System.out.printf(
                "Balance        : RM %.2f%n",
                passenger.getBalance()
        );

        System.out.println();

        /*
         * Balance is checked against the
         * actual final amount to be paid.
         */
        if (passenger.getBalance()
                < finalFare) {

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
                scanner.nextLine()
                        .trim();

        if (!confirmation
                .equalsIgnoreCase("Y")) {

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
         * Real-world purchase order:
         *
         * Payment must succeed before the Ticket
         * is created or the loyalty reward is used.
         */
        boolean paymentSuccessful =
                paymentService.processPayment(
                        payment,
                        finalFare
                );

        if (!paymentSuccessful) {

            clearScreen();

            System.out.println(
                    "Payment failed."
            );

            System.out.println(
                    "No ticket was created."
            );

            if (loyaltyDiscountApplied) {

                System.out.println(
                        "Your loyalty reward was not used."
                );
            }

            waitForBack();

            return;
        }

        try {

            /*
             * Only after successful payment do we
             * create/store the Ticket using the
             * actual amount that was paid.
             */
            Ticket ticket =
                    ticketService.buyTicket(
                            passenger,
                            selectedRoute,
                            ticketType,
                            finalFare,
                            loyaltyDiscountApplied
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

            if (loyaltyDiscountApplied) {

                System.out.println();

                System.out.println(
                        "Your one-time 20% loyalty reward"
                );

                System.out.println(
                        "was successfully used."
                );
            }

            System.out.println();

            ticket.printTicket();

            System.out.println();

            System.out.printf(
                    "Remaining Balance: RM %.2f%n",
                    passenger.getBalance()
            );

            System.out.println();

            int availableRewards =
                    loyaltyDiscountService
                            .getAvailableRewardCount(
                                    passenger
                            );

            if (availableRewards > 0) {

                System.out.println(
                        "Loyalty Status   : "
                                + availableRewards
                                + " reward(s) available"
                );

            } else {

                double progress =
                        loyaltyDiscountService
                                .getProgressTowardNextReward(
                                        passenger
                                );

                System.out.printf(
                        "Loyalty Progress : RM %.2f / RM 100.00%n",
                        progress
                );
            }

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
     * Warns Passenger about existing
     * ACTIVE Tickets.
     */
    private static boolean confirmActiveTicketWarning(
            Passenger passenger,
            Route selectedRoute,
            TicketType selectedTicketType) {

        int activeTicketCount =
                0;

        Ticket exactDuplicate =
                null;

        for (Ticket ticket :
                tickets) {

            if (ticket.getPassenger()
                    == passenger
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
                    scanner.nextLine()
                            .trim();

            if (choice
                    .equalsIgnoreCase("Y")) {

                return true;
            }

            if (choice
                    .equalsIgnoreCase("N")) {

                return false;
            }

            System.out.println(
                    "Invalid choice. Please enter Y or N."
            );
        }
    }

    /**
     * Passenger Ticket cancellation.
     */
    private static void cancelPassengerTicket(
            Passenger passenger) {

        clearScreen();

        System.out.println(
                "========== CANCEL TICKET =========="
        );

        System.out.println();

        boolean activeTicketFound =
                false;

        for (Ticket ticket :
                tickets) {

            if (ticket.getPassenger()
                    == passenger
                    && ticket.getStatus()
                    == TicketStatus.ACTIVE) {

                ticket.printTicket();

                System.out.println(
                        "-------------------------"
                );

                activeTicketFound =
                        true;
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
                scanner.nextLine()
                        .trim();

        if (ticketId
                .equalsIgnoreCase("X")) {

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
                    scanner.nextLine()
                            .trim();

            if (confirmation
                    .equalsIgnoreCase("N")) {

                showMessage(
                        "Cancellation cancelled."
                );

                return;
            }

            if (confirmation
                    .equalsIgnoreCase("Y")) {

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
     * Finds Passenger Ticket by ID.
     */
    private static Ticket findPassengerTicketById(
            Passenger passenger,
            String ticketId) {

        for (Ticket ticket :
                tickets) {

            if (ticket.getPassenger()
                    == passenger
                    && ticket.getTicketId()
                    .equalsIgnoreCase(
                            ticketId
                    )) {

                return ticket;
            }
        }

        return null;
    }

    /**
     * Ticket type selection.
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
                    scanner.nextLine()
                            .trim();

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
     * Payment method selection.
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
                    scanner.nextLine()
                            .trim();

            switch (choice) {

                case "1":
                    return new CashPayment();

                case "2":

                    System.out.print(
                            "Enter card number: "
                    );

                    String cardNumber =
                            scanner.nextLine()
                                    .trim();

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

        for (Route route :
                routes) {

            if (route.getRouteId()
                    .equalsIgnoreCase(
                            routeId
                    )) {

                return route;
            }
        }

        return null;
    }

    /**
     * Displays Passenger Tickets.
     */
    private static void viewPassengerTickets(
            Passenger passenger) {

        clearScreen();

        System.out.println(
                "========== MY TICKETS =========="
        );

        System.out.println();

        boolean found =
                false;

        for (Ticket ticket :
                tickets) {

            if (ticket.getPassenger()
                    == passenger) {

                ticket.printTicket();

                System.out.println(
                        "-------------------------"
                );

                found =
                        true;
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
     * Bonus Passenger Travel History menu.
     */
    private static void managePassengerTravelHistory(
            Passenger passenger) {

        boolean viewingHistory =
                true;

        while (viewingHistory) {

            clearScreen();

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "            TRAVEL HISTORY"
            );

            System.out.println(
                    "========================================"
            );

            System.out.println(
                    "1. Current Month"
            );

            System.out.println(
                    "2. Select Month"
            );

            System.out.println(
                    "3. All-Time History"
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
                    scanner.nextLine()
                            .trim();

            switch (choice) {

                case "1":
                    viewCurrentMonthTravelHistory(
                            passenger
                    );
                    break;

                case "2":
                    viewSelectedMonthTravelHistory(
                            passenger
                    );
                    break;

                case "3":
                    viewAllTimeTravelHistory(
                            passenger
                    );
                    break;

                case "X":
                case "x":
                    viewingHistory =
                            false;
                    break;

                default:
                    showMessage(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }

    /**
     * Displays the Passenger's current-month
     * travel history.
     */
    private static void viewCurrentMonthTravelHistory(
            Passenger passenger) {

        clearScreen();

        try {

            travelHistoryService
                    .showCurrentMonthHistory(
                            passenger
                    );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Unable to display travel history: "
                            + e.getMessage()
            );
        }

        waitForBack();
    }

    /**
     * Allows the Passenger to choose a year
     * and month for travel-history viewing.
     */
    private static void viewSelectedMonthTravelHistory(
            Passenger passenger) {

        clearScreen();

        System.out.println(
                "========== SELECT TRAVEL HISTORY PERIOD =========="
        );

        System.out.println();

        System.out.println(
                "[X] Back"
        );

        System.out.println();

        Integer year =
                readYear(
                        "Enter year: "
                );

        if (year == null) {
            return;
        }

        Integer month =
                readMonth(
                        "Enter month (1-12): "
                );

        if (month == null) {
            return;
        }

        clearScreen();

        try {

            travelHistoryService
                    .showMonthlyHistory(
                            passenger,
                            year,
                            month
                    );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Unable to display travel history: "
                            + e.getMessage()
            );
        }

        waitForBack();
    }

    /**
     * Displays all available non-deleted
     * purchase history for the Passenger.
     */
    private static void viewAllTimeTravelHistory(
            Passenger passenger) {

        clearScreen();

        try {

            travelHistoryService
                    .showAllTimeHistory(
                            passenger
                    );

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Unable to display travel history: "
                            + e.getMessage()
            );
        }

        waitForBack();
    }

    /**
     * Main menu.
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
     * Displays message and waits for X.
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
     * Waits for X before returning.
     */
    private static void waitForBack() {

        while (true) {

            System.out.println();

            System.out.print(
                    "Press X to go back: "
            );

            String choice =
                    scanner.nextLine()
                            .trim();

            if (choice
                    .equalsIgnoreCase("X")) {

                return;
            }

            System.out.println(
                    "Invalid choice. Press X to go back."
            );
        }
    }

    /**
     * Clears console screen.
     */
    private static void clearScreen() {

        try {

            String operatingSystem =
                    System.getProperty(
                            "os.name"
                    ).toLowerCase(
                            Locale.ROOT
                    );

            ProcessBuilder processBuilder;

            if (operatingSystem.contains(
                    "windows"
            )) {

                processBuilder =
                        new ProcessBuilder(
                                "cmd",
                                "/c",
                                "cls"
                        );

            } else {

                processBuilder =
                        new ProcessBuilder(
                                "clear"
                        );
            }

            processBuilder
                    .inheritIO()
                    .start()
                    .waitFor();

        } catch (Exception e) {

            /*
             * Fallback when the terminal
             * cannot be cleared.
             */
            System.out.println(
                    "\n".repeat(
                            50
                    )
            );
        }
    }
}