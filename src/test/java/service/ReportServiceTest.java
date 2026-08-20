package service;

import enums.TicketStatus;
import enums.TicketType;
import model.Station;
import model.Ticket;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ReportServiceTest {

    public static void main(String[] args) {

        Station klSentral =
                new Station(
                        "S001",
                        "KL Sentral",
                        "Kuala Lumpur"
                );

        Station pasarSeni =
                new Station(
                        "S002",
                        "Pasar Seni",
                        "Kuala Lumpur"
                );

        Station masjidJamek =
                new Station(
                        "S003",
                        "Masjid Jamek",
                        "Kuala Lumpur"
                );

        ArrayList<Ticket> tickets = new ArrayList<>();

        tickets.add(
                new Ticket(
                        "TKT001",
                        null,
                        klSentral,
                        pasarSeni,
                        TicketType.SINGLE,
                        TicketStatus.ACTIVE,
                        5.00,
                        LocalDateTime.of(2026, 8, 5, 10, 0)
                )
        );

        tickets.add(
                new Ticket(
                        "TKT002",
                        null,
                        klSentral,
                        pasarSeni,
                        TicketType.DAILY,
                        TicketStatus.CANCELLED,
                        10.00,
                        LocalDateTime.of(2026, 8, 10, 14, 30)
                )
        );

        tickets.add(
                new Ticket(
                        "TKT003",
                        null,
                        pasarSeni,
                        masjidJamek,
                        TicketType.MONTHLY,
                        TicketStatus.ACTIVE,
                        50.00,
                        LocalDateTime.of(2026, 9, 2, 9, 15)
                )
        );

        tickets.add(
                new Ticket(
                        "TKT004",
                        null,
                        klSentral,
                        pasarSeni,
                        TicketType.SINGLE,
                        TicketStatus.USED,
                        5.00,
                        LocalDateTime.of(2026, 8, 20, 18, 45)
                )
        );

        ReportService reportService =
                new ReportService(tickets);

        System.out.println(
                "===== REPORT SERVICE FUNCTIONALITY TEST ====="
        );

        System.out.println("\n--- LECTURER REQUIRED REPORTS ---");

        reportService.showTotalSales();
        reportService.showTotalRevenue();
        reportService.showCancelledTickets();

        System.out.println("\n--- EXTRA REPORTS ---");

        reportService.showTicketTypeStatistics();

        System.out.println();

        reportService.showRoutePopularity();

        System.out.println();

        reportService.showFareStatistics();

        System.out.println();

        reportService.generateMonthlyReport(2026, 8);
    }
}