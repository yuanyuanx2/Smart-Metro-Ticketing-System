package service;

import enums.TicketStatus;
import enums.TicketType;
import model.Ticket;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ReportService {

    // Lecturer-required collection
    private ArrayList<Ticket> tickets;

    public ReportService(ArrayList<Ticket> tickets) {
        this.tickets = tickets;
    }

    // Lecturer-required: display total tickets sold
    public void showTotalSales() {
        System.out.println("Total Tickets Sold: " + tickets.size());
    }

    // Lecturer-required: display total revenue
    public void showTotalRevenue() {

        double totalRevenue = 0;

        for (Ticket ticket : tickets) {
            totalRevenue += ticket.getFare();
        }

        System.out.printf("Total Revenue: RM%.2f%n", totalRevenue);
    }

    // Lecturer-required: display cancelled tickets
    public void showCancelledTickets() {

        System.out.println("===== CANCELLED TICKETS =====");

        boolean found = false;

        for (Ticket ticket : tickets) {

            if (ticket.getStatus() == TicketStatus.CANCELLED) {

                found = true;

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
            System.out.println("No cancelled tickets.");
        }
    }

    // Extra 1: ticket type breakdown
    public void showTicketTypeStatistics() {

        Map<TicketType, Integer> typeCounts =
                new EnumMap<>(TicketType.class);

        for (TicketType type : TicketType.values()) {
            typeCounts.put(type, 0);
        }

        for (Ticket ticket : tickets) {
            TicketType type = ticket.getTicketType();
            typeCounts.put(type, typeCounts.get(type) + 1);
        }

        System.out.println("===== TICKET TYPE STATISTICS =====");

        for (TicketType type : TicketType.values()) {
            System.out.println(type + ": " + typeCounts.get(type));
        }
    }

    // Extra 2: route popularity / ranking
    public void showRoutePopularity() {

        Map<String, Integer> routeCounts = new HashMap<>();

        for (Ticket ticket : tickets) {

            String route =
                    ticket.getSource().getName()
                            + " -> "
                            + ticket.getDestination().getName();

            routeCounts.put(
                    route,
                    routeCounts.getOrDefault(route, 0) + 1
            );
        }

        System.out.println("===== ROUTE POPULARITY =====");

        if (routeCounts.isEmpty()) {
            System.out.println("No ticket data available.");
            return;
        }

        routeCounts.entrySet()
                .stream()
                .sorted((route1, route2) ->
                        Integer.compare(
                                route2.getValue(),
                                route1.getValue()
                        ))
                .forEach(entry ->
                        System.out.println(
                                entry.getKey()
                                        + ": "
                                        + entry.getValue()
                                        + " ticket(s)"
                        ));
    }

    // Extra 3: average, minimum and maximum fare
    public void showFareStatistics() {

        if (tickets.isEmpty()) {
            System.out.println("No ticket data available.");
            return;
        }

        double total = 0;
        double minimum = tickets.get(0).getFare();
        double maximum = tickets.get(0).getFare();

        for (Ticket ticket : tickets) {

            double fare = ticket.getFare();

            total += fare;

            if (fare < minimum) {
                minimum = fare;
            }

            if (fare > maximum) {
                maximum = fare;
            }
        }

        double average = total / tickets.size();

        System.out.println("===== FARE STATISTICS =====");
        System.out.printf("Average Fare: RM%.2f%n", average);
        System.out.printf("Minimum Fare: RM%.2f%n", minimum);
        System.out.printf("Maximum Fare: RM%.2f%n", maximum);
    }

    // Extra 4: monthly report
    public void generateMonthlyReport(int year, int month) {

        if (month < 1 || month > 12) {
            System.out.println("Invalid month.");
            return;
        }

        YearMonth selectedMonth = YearMonth.of(year, month);

        int totalSales = 0;
        int cancelledTickets = 0;
        double totalRevenue = 0;

        Map<TicketType, Integer> typeCounts =
                new EnumMap<>(TicketType.class);

        for (TicketType type : TicketType.values()) {
            typeCounts.put(type, 0);
        }

        for (Ticket ticket : tickets) {

            YearMonth ticketMonth =
                    YearMonth.from(ticket.getPurchaseDateTime());

            if (ticketMonth.equals(selectedMonth)) {

                totalSales++;
                totalRevenue += ticket.getFare();

                TicketType type = ticket.getTicketType();

                typeCounts.put(
                        type,
                        typeCounts.get(type) + 1
                );

                if (ticket.getStatus() == TicketStatus.CANCELLED) {
                    cancelledTickets++;
                }
            }
        }

        System.out.println(
                "===== MONTHLY REPORT: "
                        + selectedMonth
                        + " ====="
        );

        System.out.println("Tickets Sold: " + totalSales);
        System.out.printf("Revenue: RM%.2f%n", totalRevenue);
        System.out.println("Cancelled Tickets: " + cancelledTickets);

        System.out.println("--- Ticket Types ---");

        for (TicketType type : TicketType.values()) {
            System.out.println(type + ": " + typeCounts.get(type));
        }
    }
}