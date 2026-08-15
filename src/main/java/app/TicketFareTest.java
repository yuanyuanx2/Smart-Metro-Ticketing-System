package app;

import enums.TicketStatus;
import enums.TicketType;
import exception.TicketNotFoundException;
import fare.FareCalculator;
import fare.StandardFareCalculator;
import model.Passenger;
import model.Route;
import model.Station;
import model.Ticket;
import service.TicketService;

public class TicketFareTest {

    public static void main(String[] args) {

        // 1. Create stations and a route for ticket testing
        Station station1 = new Station(
                "S001",
                "KL Sentral",
                "Kuala Lumpur"
        );

        Station station2 = new Station(
                "S002",
                "Pasar Seni",
                "Kuala Lumpur"
        );

        Route route = new Route(
                "R001",
                station1,
                station2,
                10.0
        );

        // 2. Create the fare calculator and ticket service
        FareCalculator fareCalculator = new StandardFareCalculator();
        TicketService ticketService = new TicketService(fareCalculator);

        // 3. Create a passenger
        Passenger passenger = new Passenger(
                "P001",
                "Ali",
                "ali@email.com",
                "1234"
        );

        System.out.println("===== TICKET AND FARE TEST =====");

        // TEST 1 - Calculate SINGLE ticket fare
        System.out.println("\nTEST 1 - Calculate SINGLE ticket fare");
        double singleFare = fareCalculator.calculateFare(
                route,
                TicketType.SINGLE
        );
        System.out.printf("Fare: RM %.2f%n", singleFare);

        // TEST 2 - Calculate DAILY ticket fare
        System.out.println("\nTEST 2 - Calculate DAILY ticket fare");
        double dailyFare = fareCalculator.calculateFare(
                route,
                TicketType.DAILY
        );
        System.out.printf("Fare: RM %.2f%n", dailyFare);

        // TEST 3 - Calculate MONTHLY ticket fare
        System.out.println("\nTEST 3 - Calculate MONTHLY ticket fare");
        double monthlyFare = fareCalculator.calculateFare(
                route,
                TicketType.MONTHLY
        );
        System.out.printf("Fare: RM %.2f%n", monthlyFare);

        // TEST 4 - Successful ticket purchase
        System.out.println("\nTEST 4 - Successful ticket purchase");

        passenger.topUp(100.00);

        double balanceBefore = passenger.getBalance();

        Ticket ticket = ticketService.buyTicket(
                passenger,
                route,
                TicketType.SINGLE
        );

        double balanceAfter = passenger.getBalance();

        System.out.println("Ticket ID: " + ticket.getTicketId());
        System.out.println("Ticket status: " + ticket.getStatus());
        System.out.printf("Balance before purchase: RM %.2f%n", balanceBefore);
        System.out.printf("Ticket fare: RM %.2f%n", ticket.getFare());
        System.out.printf("Balance after purchase: RM %.2f%n", balanceAfter);

        // TEST 5 - View stored tickets
        System.out.println("\nTEST 5 - View stored tickets");
        ticketService.viewTickets();

        // TEST 6 - Cancel existing ticket
        System.out.println("\nTEST 6 - Cancel existing ticket");

        try {
            ticketService.cancelTicket(ticket.getTicketId());

            System.out.println(
                    "Ticket status after cancellation: "
                            + ticket.getStatus()
            );

        } catch (TicketNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // TEST 7 - Reject nonexistent ticket ID
        System.out.println("\nTEST 7 - Reject nonexistent ticket ID");

        try {
            ticketService.cancelTicket("TKT999");
        } catch (TicketNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // TEST 8 - Reject purchase with insufficient balance
        System.out.println(
                "\nTEST 8 - Reject purchase with insufficient balance"
        );

        Passenger poorPassenger = new Passenger(
                "P002",
                "Ben",
                "ben@email.com",
                "1234"
        );

        try {
            ticketService.buyTicket(
                    poorPassenger,
                    route,
                    TicketType.SINGLE
            );
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}