package app;

import enums.TicketType;
import exception.TicketNotFoundException;
import fare.FareCalculator;
import fare.StandardFareCalculator;
import model.Passenger;
import model.Route;
import model.Station;
import model.Ticket;
import service.TicketService;

public class TicketFareFinalTest {

    public static void main(String[] args) {

        // 1. Create stations and route for final testing
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

        // 2. Create fare calculator and ticket service
        FareCalculator fareCalculator = new StandardFareCalculator();
        TicketService ticketService = new TicketService(fareCalculator);

        System.out.println("===== FINAL TICKET AND FARE TEST =====");

        // TEST 1 - Empty ticket list
        System.out.println("\nTEST 1 - Empty ticket list");
        ticketService.viewTickets();

        // TEST 2 - Calculate SINGLE fare
        System.out.println("\nTEST 2 - Calculate SINGLE fare");
        System.out.printf(
                "SINGLE fare: RM %.2f%n",
                fareCalculator.calculateFare(route, TicketType.SINGLE)
        );

        // TEST 3 - Calculate DAILY fare
        System.out.println("\nTEST 3 - Calculate DAILY fare");
        System.out.printf(
                "DAILY fare: RM %.2f%n",
                fareCalculator.calculateFare(route, TicketType.DAILY)
        );

        // TEST 4 - Calculate MONTHLY fare
        System.out.println("\nTEST 4 - Calculate MONTHLY fare");
        System.out.printf(
                "MONTHLY fare: RM %.2f%n",
                fareCalculator.calculateFare(route, TicketType.MONTHLY)
        );

        // Create the first passenger
        Passenger passenger1 = new Passenger(
                "P001",
                "Ali",
                "ali@email.com",
                "1234"
        );

        passenger1.topUp(100.00);

        // TEST 5 - First successful purchase
        System.out.println("\nTEST 5 - First successful purchase");

        Ticket ticket1 = ticketService.buyTicket(
                passenger1,
                route,
                TicketType.SINGLE
        );

        System.out.println("Ticket ID: " + ticket1.getTicketId());
        System.out.println("Status: " + ticket1.getStatus());
        System.out.printf("Fare: RM %.2f%n", ticket1.getFare());

        // TEST 6 - Second successful purchase
        System.out.println("\nTEST 6 - Second successful purchase");

        Ticket ticket2 = ticketService.buyTicket(
                passenger1,
                route,
                TicketType.DAILY
        );

        System.out.println("Ticket ID: " + ticket2.getTicketId());
        System.out.println("Status: " + ticket2.getStatus());
        System.out.printf("Fare: RM %.2f%n", ticket2.getFare());

        // TEST 7 - Correct balance deduction
        System.out.println("\nTEST 7 - Correct balance deduction");
        System.out.printf(
                "Passenger balance after two purchases: RM %.2f%n",
                passenger1.getBalance()
        );

        // Create a passenger with insufficient balance
        Passenger poorPassenger = new Passenger(
                "P002",
                "Ben",
                "ben@email.com",
                "1234"
        );

        // TEST 8 - Reject insufficient balance
        System.out.println("\nTEST 8 - Reject insufficient balance");

        try {
            ticketService.buyTicket(
                    poorPassenger,
                    route,
                    TicketType.SINGLE
            );
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        // TEST 9 - Failed purchase is not stored
        System.out.println("\nTEST 9 - Failed purchase is not stored");
        System.out.println(
                "Only TKT001 and TKT002 should appear below:"
        );
        ticketService.viewTickets();

        // Create a passenger whose balance exactly equals the SINGLE fare
        Passenger exactBalancePassenger = new Passenger(
                "P003",
                "Chong",
                "chong@email.com",
                "1234"
        );

        exactBalancePassenger.topUp(5.00);

        // TEST 10 - Balance exactly equal to fare
        System.out.println(
                "\nTEST 10 - Balance exactly equal to fare"
        );

        Ticket ticket3 = ticketService.buyTicket(
                exactBalancePassenger,
                route,
                TicketType.SINGLE
        );

        System.out.println("Ticket ID: " + ticket3.getTicketId());
        System.out.printf(
                "Remaining balance: RM %.2f%n",
                exactBalancePassenger.getBalance()
        );

        // TEST 11 - View multiple stored tickets
        System.out.println("\nTEST 11 - View multiple stored tickets");
        ticketService.viewTickets();

        // TEST 12 - Cancel existing ticket
        System.out.println("\nTEST 12 - Cancel existing ticket");

        try {
            ticketService.cancelTicket(ticket2.getTicketId());

            System.out.println(
                    "Ticket " + ticket2.getTicketId()
                            + " status: " + ticket2.getStatus()
            );

        } catch (TicketNotFoundException e) {
            System.out.println(e.getMessage());
        }

        // TEST 13 - View cancelled ticket
        System.out.println("\nTEST 13 - View cancelled ticket");

        ticket2.printTicket();

        // TEST 14 - Reject nonexistent ticket ID
        System.out.println(
                "\nTEST 14 - Reject nonexistent ticket ID"
        );

        try {
            ticketService.cancelTicket("TKT999");
        } catch (TicketNotFoundException e) {
            System.out.println(e.getMessage());
        }

        System.out.println(
                "\n===== FINAL TICKET AND FARE TEST COMPLETED ====="
        );
    }
}