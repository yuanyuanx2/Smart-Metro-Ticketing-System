package service;

import enums.TicketStatus;
import enums.TicketType;
import model.Station;
import model.Ticket;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ReportFinalTest {

    public static void main(String[] args) {

        Station klSentral =
                new Station("S001", "KL Sentral", "Kuala Lumpur");

        Station pasarSeni =
                new Station("S002", "Pasar Seni", "Kuala Lumpur");

        Station masjidJamek =
                new Station("S003", "Masjid Jamek", "Kuala Lumpur");

        ArrayList<Ticket> tickets = new ArrayList<>();

        tickets.add(new Ticket(
                "TKT001",
                null,
                klSentral,
                pasarSeni,
                TicketType.SINGLE,
                TicketStatus.ACTIVE,
                5.00,
                LocalDateTime.of(2026, 8, 5, 10, 0)
        ));

        tickets.add(new Ticket(
                "TKT002",
                null,
                klSentral,
                pasarSeni,
                TicketType.DAILY,
                TicketStatus.CANCELLED,
                10.00,
                LocalDateTime.of(2026, 8, 10, 14, 30)
        ));

        tickets.add(new Ticket(
                "TKT003",
                null,
                pasarSeni,
                masjidJamek,
                TicketType.MONTHLY,
                TicketStatus.ACTIVE,
                50.00,
                LocalDateTime.of(2026, 9, 2, 9, 15)
        ));

        tickets.add(new Ticket(
                "TKT004",
                null,
                klSentral,
                pasarSeni,
                TicketType.SINGLE,
                TicketStatus.USED,
                5.00,
                LocalDateTime.of(2026, 8, 20, 18, 45)
        ));

        ReportService reportService =
                new ReportService(tickets);

        System.out.println(
                "===== FINAL REPORTING FUNCTIONALITY TEST ====="
        );

        // Lecturer-required reports
        System.out.println(
                "\n===== TEST 1: LECTURER REQUIRED REPORTS ====="
        );

        reportService.showTotalSales();
        reportService.showTotalRevenue();
        reportService.showCancelledTickets();

        // Extra reporting features
        System.out.println(
                "\n===== TEST 2: TICKET TYPE STATISTICS ====="
        );

        reportService.showTicketTypeStatistics();

        System.out.println(
                "\n===== TEST 3: ROUTE POPULARITY ====="
        );

        reportService.showRoutePopularity();

        System.out.println(
                "\n===== TEST 4: FARE STATISTICS ====="
        );

        reportService.showFareStatistics();

        // Month containing tickets
        System.out.println(
                "\n===== TEST 5: AUGUST MONTHLY REPORT ====="
        );

        reportService.generateMonthlyReport(2026, 8);

        // Another month containing tickets
        System.out.println(
                "\n===== TEST 6: SEPTEMBER MONTHLY REPORT ====="
        );

        reportService.generateMonthlyReport(2026, 9);

        // Month containing no tickets
        System.out.println(
                "\n===== TEST 7: MONTH WITH NO TICKETS ====="
        );

        reportService.generateMonthlyReport(2026, 7);

        // Invalid month
        System.out.println(
                "\n===== TEST 8: INVALID MONTH ====="
        );

        reportService.generateMonthlyReport(2026, 13);

        // Empty ticket collection
        System.out.println(
                "\n===== TEST 9: EMPTY TICKET LIST ====="
        );

        ArrayList<Ticket> emptyTickets =
                new ArrayList<>();

        ReportService emptyReportService =
                new ReportService(emptyTickets);

        emptyReportService.showTotalSales();
        emptyReportService.showTotalRevenue();
        emptyReportService.showCancelledTickets();
        emptyReportService.showTicketTypeStatistics();
        emptyReportService.showRoutePopularity();
        emptyReportService.showFareStatistics();
        emptyReportService.generateMonthlyReport(2026, 8);
    }
}