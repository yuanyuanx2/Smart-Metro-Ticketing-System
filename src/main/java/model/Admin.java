package model;

import enums.UserRole;
import service.ReportService;
import service.StationService;
import service.TrainService;

/**
 * Represents an administrator in the Smart Metro Ticketing System.
 * Admin inherits the common user information and behaviours
 * from the User class.
 */
public class Admin extends User {

    private StationService stationService;
    private TrainService trainService;
    private ReportService reportService;

    /**
     * Creates an administrator account.
     * This constructor is retained for user registration
     * and file persistence compatibility.
     */
    public Admin(String userId, String name, String email, String password) {

        super(userId, name, email, password, UserRole.ADMIN);
    }

    /**
     * Creates an administrator connected to the system services.
     */
    public Admin(
            String userId,
            String name,
            String email,
            String password,
            StationService stationService,
            TrainService trainService,
            ReportService reportService) {

        super(userId, name, email, password, UserRole.ADMIN);

        this.stationService = stationService;
        this.trainService = trainService;
        this.reportService = reportService;
    }

    /**
     * Adds a station through the station service.
     */
    public void addStation(Station station) {

        if (stationService == null) {
            throw new IllegalStateException(
                    "Station service is not available."
            );
        }

        stationService.addStation(station);
    }

    /**
     * Adds a train through the train service.
     */
    public void addTrain(Train train) {

        if (trainService == null) {
            throw new IllegalStateException(
                    "Train service is not available."
            );
        }

        trainService.addTrain(train);
    }

    /**
     * Displays the lecturer-required ticket reports.
     */
    public void viewReports() {

        if (reportService == null) {
            throw new IllegalStateException(
                    "Report service is not available."
            );
        }

        reportService.showTotalSales();
        reportService.showTotalRevenue();
        reportService.showCancelledTickets();
    }
}