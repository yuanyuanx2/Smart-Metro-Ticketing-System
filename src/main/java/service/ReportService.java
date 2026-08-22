package service;

import enums.TicketStatus;
import enums.TicketType;
import model.Ticket;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles ticket reporting and statistics.
 */
public class ReportService {

    /*
     * Lecturer-required live Ticket collection.
     */
    private ArrayList<Ticket> tickets;

    private static final DateTimeFormatter REPORT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy hh:mm a"
            );

    /**
     * Creates a ReportService using
     * the shared live Ticket collection.
     */
    public ReportService(
            ArrayList<Ticket> tickets) {

        if (tickets == null) {
            this.tickets =
                    new ArrayList<>();
        } else {
            this.tickets =
                    tickets;
        }
    }

    /**
     * Lecturer-required:
     * displays total Tickets sold.
     */
    public void showTotalSales() {

        System.out.println(
                "Total Tickets Sold: "
                        + tickets.size()
        );
    }

    /**
     * Lecturer-required:
     * displays gross revenue.
     *
     * Cancelled Tickets remain part of gross revenue
     * because this system currently has no refund feature.
     */
    public void showTotalRevenue() {

        double totalRevenue =
                calculateTotalRevenue(
                        tickets
                );

        System.out.printf(
                "Total Revenue: RM%.2f%n",
                totalRevenue
        );
    }

    /**
     * Lecturer-required:
     * displays cancelled Tickets.
     */
    public void showCancelledTickets() {

        System.out.println(
                "===== CANCELLED TICKETS ====="
        );

        boolean found =
                false;

        for (Ticket ticket : tickets) {

            if (ticket.getStatus()
                    == TicketStatus.CANCELLED) {

                found =
                        true;

                System.out.printf(
                        "%s | %s -> %s | %s | RM%.2f%n",
                        ticket.getTicketId(),
                        ticket.getSource().getName(),
                        ticket.getDestination().getName(),
                        ticket.getTicketType(),
                        ticket.getFare()
                );
            }
        }

        if (!found) {

            System.out.println(
                    "No cancelled tickets."
            );
        }
    }

    /**
     * Extra report:
     * Ticket sales grouped by Ticket type.
     */
    public void showTicketTypeStatistics() {

        System.out.println(
                "===== TICKET TYPE STATISTICS ====="
        );

        displayTicketTypeStatistics(
                tickets
        );
    }

    /**
     * Extra report:
     * Routes ranked by Tickets sold.
     */
    public void showRoutePopularity() {

        System.out.println(
                "===== ROUTE POPULARITY ====="
        );

        displayRoutePopularity(
                tickets
        );
    }

    /**
     * Extra report:
     * average, minimum and maximum fares.
     */
    public void showFareStatistics() {

        System.out.println(
                "===== FARE STATISTICS ====="
        );

        displayFareStatistics(
                tickets
        );
    }

    /**
     * Displays a report for one calendar month.
     */
    public void generateMonthlyReport(
            int year,
            int month) {

        if (!isValidYear(year)) {

            System.out.println(
                    "Invalid year."
            );

            return;
        }

        if (month < 1
                || month > 12) {

            System.out.println(
                    "Invalid month."
            );

            return;
        }

        ArrayList<Ticket> periodTickets =
                getMonthlyTickets(
                        year,
                        month
                );

        String title =
                String.format(
                        "MONTHLY REPORT: %04d-%02d",
                        year,
                        month
                );

        displayPeriodReport(
                title,
                periodTickets
        );
    }

    /**
     * Displays a report for one quarter.
     *
     * Q1 = January - March
     * Q2 = April - June
     * Q3 = July - September
     * Q4 = October - December
     */
    public void generateQuarterlyReport(
            int year,
            int quarter) {

        if (!isValidYear(year)) {

            System.out.println(
                    "Invalid year."
            );

            return;
        }

        if (quarter < 1
                || quarter > 4) {

            System.out.println(
                    "Invalid quarter."
            );

            return;
        }

        ArrayList<Ticket> periodTickets =
                getQuarterlyTickets(
                        year,
                        quarter
                );

        String title =
                "QUARTERLY REPORT: "
                        + year
                        + " Q"
                        + quarter;

        displayPeriodReport(
                title,
                periodTickets
        );
    }

    /**
     * Displays a report for one calendar year.
     */
    public void generateYearlyReport(
            int year) {

        if (!isValidYear(year)) {

            System.out.println(
                    "Invalid year."
            );

            return;
        }

        ArrayList<Ticket> periodTickets =
                getYearlyTickets(
                        year
                );

        String title =
                "YEARLY REPORT: "
                        + year;

        displayPeriodReport(
                title,
                periodTickets
        );
    }

    /**
     * Builds the all-time report
     * for TXT/PDF exporting.
     */
    public ArrayList<String> buildExportReport() {

        return buildPeriodExportReport(
                "ALL-TIME SYSTEM REPORT",
                new ArrayList<>(
                        tickets
                )
        );
    }

    /**
     * Builds one monthly report
     * for TXT/PDF exporting.
     */
    public ArrayList<String> buildMonthlyExportReport(
            int year,
            int month) {

        if (!isValidYear(year)) {

            throw new IllegalArgumentException(
                    "Invalid year."
            );
        }

        if (month < 1
                || month > 12) {

            throw new IllegalArgumentException(
                    "Invalid month."
            );
        }

        ArrayList<Ticket> periodTickets =
                getMonthlyTickets(
                        year,
                        month
                );

        String title =
                String.format(
                        "MONTHLY REPORT: %04d-%02d",
                        year,
                        month
                );

        return buildPeriodExportReport(
                title,
                periodTickets
        );
    }

    /**
     * Builds one quarterly report
     * for TXT/PDF exporting.
     */
    public ArrayList<String> buildQuarterlyExportReport(
            int year,
            int quarter) {

        if (!isValidYear(year)) {

            throw new IllegalArgumentException(
                    "Invalid year."
            );
        }

        if (quarter < 1
                || quarter > 4) {

            throw new IllegalArgumentException(
                    "Invalid quarter."
            );
        }

        ArrayList<Ticket> periodTickets =
                getQuarterlyTickets(
                        year,
                        quarter
                );

        String title =
                "QUARTERLY REPORT: "
                        + year
                        + " Q"
                        + quarter;

        return buildPeriodExportReport(
                title,
                periodTickets
        );
    }

    /**
     * Builds one yearly report
     * for TXT/PDF exporting.
     */
    public ArrayList<String> buildYearlyExportReport(
            int year) {

        if (!isValidYear(year)) {

            throw new IllegalArgumentException(
                    "Invalid year."
            );
        }

        ArrayList<Ticket> periodTickets =
                getYearlyTickets(
                        year
                );

        String title =
                "YEARLY REPORT: "
                        + year;

        return buildPeriodExportReport(
                title,
                periodTickets
        );
    }

    /**
     * Displays a standard management report
     * for a selected reporting period.
     */
    private void displayPeriodReport(
            String title,
            ArrayList<Ticket> periodTickets) {

        System.out.println(
                "===== "
                        + title
                        + " ====="
        );

        System.out.println();

        System.out.println(
                "Tickets Sold: "
                        + periodTickets.size()
        );

        System.out.printf(
                "Gross Revenue: RM%.2f%n",
                calculateTotalRevenue(
                        periodTickets
                )
        );

        System.out.println(
                "Cancelled Tickets: "
                        + countCancelledTickets(
                        periodTickets
                )
        );

        System.out.println();

        System.out.println(
                "--- Ticket Types ---"
        );

        displayTicketTypeStatistics(
                periodTickets
        );

        System.out.println();

        System.out.println(
                "--- Route Popularity ---"
        );

        displayRoutePopularity(
                periodTickets
        );

        System.out.println();

        System.out.println(
                "--- Fare Statistics ---"
        );

        displayFareStatistics(
                periodTickets
        );
    }

    /**
     * Builds an exportable management report
     * for the supplied Ticket collection.
     */
    private ArrayList<String> buildPeriodExportReport(
            String title,
            ArrayList<Ticket> periodTickets) {

        ArrayList<String> report =
                new ArrayList<>();

        report.add(
                "SMART METRO TICKETING SYSTEM"
        );

        report.add(
                title
        );

        report.add(
                "Generated: "
                        + LocalDateTime.now()
                        .format(
                                REPORT_DATE_TIME_FORMAT
                        )
        );

        report.add(
                "========================================"
        );

        report.add(
                ""
        );

        /*
         * Summary.
         */
        report.add(
                "SUMMARY"
        );

        report.add(
                "Tickets Sold: "
                        + periodTickets.size()
        );

        report.add(
                String.format(
                        "Gross Revenue: RM%.2f",
                        calculateTotalRevenue(
                                periodTickets
                        )
                )
        );

        report.add(
                "Cancelled Tickets: "
                        + countCancelledTickets(
                        periodTickets
                )
        );

        report.add(
                ""
        );

        /*
         * Ticket-type breakdown.
         */
        report.add(
                "TICKET TYPE STATISTICS"
        );

        Map<TicketType, Integer> typeCounts =
                calculateTicketTypeCounts(
                        periodTickets
                );

        for (TicketType type :
                TicketType.values()) {

            report.add(
                    type
                            + ": "
                            + typeCounts.get(type)
            );
        }

        report.add(
                ""
        );

        /*
         * Route popularity.
         */
        report.add(
                "ROUTE POPULARITY"
        );

        Map<String, Integer> routeCounts =
                calculateRouteCounts(
                        periodTickets
                );

        if (routeCounts.isEmpty()) {

            report.add(
                    "No ticket data available."
            );

        } else {

            routeCounts.entrySet()
                    .stream()
                    .sorted(
                            (route1, route2) ->
                                    Integer.compare(
                                            route2.getValue(),
                                            route1.getValue()
                                    )
                    )
                    .forEach(
                            entry ->
                                    report.add(
                                            entry.getKey()
                                                    + ": "
                                                    + entry.getValue()
                                                    + " ticket(s)"
                                    )
                    );
        }

        report.add(
                ""
        );

        /*
         * Fare statistics.
         */
        report.add(
                "FARE STATISTICS"
        );

        if (periodTickets.isEmpty()) {

            report.add(
                    "No ticket data available."
            );

        } else {

            double total = 0;

            double minimum =
                    periodTickets.get(0)
                            .getFare();

            double maximum =
                    periodTickets.get(0)
                            .getFare();

            for (Ticket ticket :
                    periodTickets) {

                double fare =
                        ticket.getFare();

                total += fare;

                if (fare < minimum) {
                    minimum = fare;
                }

                if (fare > maximum) {
                    maximum = fare;
                }
            }

            double average =
                    total
                            / periodTickets.size();

            report.add(
                    String.format(
                            "Average Fare: RM%.2f",
                            average
                    )
            );

            report.add(
                    String.format(
                            "Minimum Fare: RM%.2f",
                            minimum
                    )
            );

            report.add(
                    String.format(
                            "Maximum Fare: RM%.2f",
                            maximum
                    )
            );
        }

        report.add(
                ""
        );

        /*
         * Cancelled Ticket details.
         */
        report.add(
                "CANCELLED TICKETS"
        );

        boolean cancelledFound =
                false;

        for (Ticket ticket :
                periodTickets) {

            if (ticket.getStatus()
                    == TicketStatus.CANCELLED) {

                cancelledFound =
                        true;

                report.add(
                        String.format(
                                "%s | %s -> %s | %s | RM%.2f",
                                ticket.getTicketId(),
                                ticket.getSource().getName(),
                                ticket.getDestination().getName(),
                                ticket.getTicketType(),
                                ticket.getFare()
                        )
                );
            }
        }

        if (!cancelledFound) {

            report.add(
                    "No cancelled tickets."
            );
        }

        report.add(
                ""
        );

        /*
         * Complete Ticket details for the period.
         */
        report.add(
                "TICKET DETAILS"
        );

        if (periodTickets.isEmpty()) {

            report.add(
                    "No tickets available."
            );

        } else {

            for (Ticket ticket :
                    periodTickets) {

                report.add(
                        String.format(
                                "%s | %s -> %s | %s | %s | RM%.2f | %s",
                                ticket.getTicketId(),
                                ticket.getSource().getName(),
                                ticket.getDestination().getName(),
                                ticket.getTicketType(),
                                ticket.getStatus(),
                                ticket.getFare(),
                                ticket.getPurchaseDateTime()
                                        .format(
                                                REPORT_DATE_TIME_FORMAT
                                        )
                        )
                );
            }
        }

        report.add(
                ""
        );

        report.add(
                "========================================"
        );

        return report;
    }

    /**
     * Returns all Tickets purchased
     * in one month.
     */
    private ArrayList<Ticket> getMonthlyTickets(
            int year,
            int month) {

        ArrayList<Ticket> result =
                new ArrayList<>();

        for (Ticket ticket : tickets) {

            LocalDateTime purchaseDateTime =
                    ticket.getPurchaseDateTime();

            if (purchaseDateTime.getYear()
                    == year
                    && purchaseDateTime
                    .getMonthValue()
                    == month) {

                result.add(
                        ticket
                );
            }
        }

        return result;
    }

    /**
     * Returns all Tickets purchased
     * in one quarter.
     */
    private ArrayList<Ticket> getQuarterlyTickets(
            int year,
            int quarter) {

        ArrayList<Ticket> result =
                new ArrayList<>();

        int startMonth =
                ((quarter - 1) * 3)
                        + 1;

        int endMonth =
                startMonth + 2;

        for (Ticket ticket : tickets) {

            LocalDateTime purchaseDateTime =
                    ticket.getPurchaseDateTime();

            int ticketMonth =
                    purchaseDateTime
                            .getMonthValue();

            if (purchaseDateTime.getYear()
                    == year
                    && ticketMonth
                    >= startMonth
                    && ticketMonth
                    <= endMonth) {

                result.add(
                        ticket
                );
            }
        }

        return result;
    }

    /**
     * Returns all Tickets purchased
     * in one calendar year.
     */
    private ArrayList<Ticket> getYearlyTickets(
            int year) {

        ArrayList<Ticket> result =
                new ArrayList<>();

        for (Ticket ticket : tickets) {

            if (ticket.getPurchaseDateTime()
                    .getYear()
                    == year) {

                result.add(
                        ticket
                );
            }
        }

        return result;
    }

    /**
     * Calculates gross Ticket revenue.
     */
    private double calculateTotalRevenue(
            ArrayList<Ticket> selectedTickets) {

        double totalRevenue =
                0;

        for (Ticket ticket :
                selectedTickets) {

            totalRevenue +=
                    ticket.getFare();
        }

        return totalRevenue;
    }

    /**
     * Counts cancelled Tickets.
     */
    private int countCancelledTickets(
            ArrayList<Ticket> selectedTickets) {

        int cancelledCount =
                0;

        for (Ticket ticket :
                selectedTickets) {

            if (ticket.getStatus()
                    == TicketStatus.CANCELLED) {

                cancelledCount++;
            }
        }

        return cancelledCount;
    }

    /**
     * Calculates Ticket counts
     * grouped by Ticket type.
     */
    private Map<TicketType, Integer>
    calculateTicketTypeCounts(
            ArrayList<Ticket> selectedTickets) {

        Map<TicketType, Integer> typeCounts =
                new EnumMap<>(
                        TicketType.class
                );

        for (TicketType type :
                TicketType.values()) {

            typeCounts.put(
                    type,
                    0
            );
        }

        for (Ticket ticket :
                selectedTickets) {

            TicketType type =
                    ticket.getTicketType();

            typeCounts.put(
                    type,
                    typeCounts.get(type) + 1
            );
        }

        return typeCounts;
    }

    /**
     * Calculates Ticket counts
     * grouped by Route.
     */
    private Map<String, Integer>
    calculateRouteCounts(
            ArrayList<Ticket> selectedTickets) {

        Map<String, Integer> routeCounts =
                new HashMap<>();

        for (Ticket ticket :
                selectedTickets) {

            String route =
                    ticket.getSource().getName()
                            + " -> "
                            + ticket.getDestination().getName();

            routeCounts.put(
                    route,
                    routeCounts.getOrDefault(
                            route,
                            0
                    ) + 1
            );
        }

        return routeCounts;
    }

    /**
     * Displays Ticket-type statistics.
     */
    private void displayTicketTypeStatistics(
            ArrayList<Ticket> selectedTickets) {

        Map<TicketType, Integer> typeCounts =
                calculateTicketTypeCounts(
                        selectedTickets
                );

        for (TicketType type :
                TicketType.values()) {

            System.out.println(
                    type
                            + ": "
                            + typeCounts.get(type)
            );
        }
    }

    /**
     * Displays Route-popularity statistics.
     */
    private void displayRoutePopularity(
            ArrayList<Ticket> selectedTickets) {

        Map<String, Integer> routeCounts =
                calculateRouteCounts(
                        selectedTickets
                );

        if (routeCounts.isEmpty()) {

            System.out.println(
                    "No ticket data available."
            );

            return;
        }

        routeCounts.entrySet()
                .stream()
                .sorted(
                        (route1, route2) ->
                                Integer.compare(
                                        route2.getValue(),
                                        route1.getValue()
                                )
                )
                .forEach(
                        entry ->
                                System.out.println(
                                        entry.getKey()
                                                + ": "
                                                + entry.getValue()
                                                + " ticket(s)"
                                )
                );
    }

    /**
     * Displays fare statistics.
     */
    private void displayFareStatistics(
            ArrayList<Ticket> selectedTickets) {

        if (selectedTickets.isEmpty()) {

            System.out.println(
                    "No ticket data available."
            );

            return;
        }

        double total =
                0;

        double minimum =
                selectedTickets.get(0)
                        .getFare();

        double maximum =
                selectedTickets.get(0)
                        .getFare();

        for (Ticket ticket :
                selectedTickets) {

            double fare =
                    ticket.getFare();

            total +=
                    fare;

            if (fare < minimum) {
                minimum = fare;
            }

            if (fare > maximum) {
                maximum = fare;
            }
        }

        double average =
                total
                        / selectedTickets.size();

        System.out.printf(
                "Average Fare: RM%.2f%n",
                average
        );

        System.out.printf(
                "Minimum Fare: RM%.2f%n",
                minimum
        );

        System.out.printf(
                "Maximum Fare: RM%.2f%n",
                maximum
        );
    }

    /**
     * Validates a calendar year.
     */
    private boolean isValidYear(
            int year) {

        return year >= 1
                && year <= 9999;
    }
}