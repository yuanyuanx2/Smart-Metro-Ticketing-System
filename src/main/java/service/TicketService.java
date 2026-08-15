package service;

import enums.TicketStatus;
import enums.TicketType;
import exception.TicketNotFoundException;
import fare.FareCalculator;
import model.Passenger;
import model.Route;
import model.Ticket;

import java.util.ArrayList;

/**
 * Manages ticket purchasing, cancellation, and viewing.
 */
public class TicketService {

    private ArrayList<Ticket> tickets;
    private FareCalculator fareCalculator;

    /**
     * Creates a ticket service using the provided fare calculator.
     */
    public TicketService(FareCalculator fareCalculator) {
        this.tickets = new ArrayList<>();
        this.fareCalculator = fareCalculator;
    }

    /**
     * Creates and stores a new ticket for a passenger.
     */
    public Ticket buyTicket(Passenger passenger, Route route, TicketType type) {

        // Calculate the fare using the FareCalculator interface.
        double fare = fareCalculator.calculateFare(route, type);

        // Generate a simple unique ticket ID.
        String ticketId = String.format("TKT%03d", tickets.size() + 1);

        // Create a new active ticket.
        Ticket ticket = new Ticket(
                ticketId,
                passenger,
                route.getSource(),
                route.getDestination(),
                type,
                TicketStatus.ACTIVE,
                fare
        );

        // Store the new ticket.
        tickets.add(ticket);

        return ticket;
    }

    /**
     * Cancels a ticket using its ticket ID.
     */
    public void cancelTicket(String ticketId) throws TicketNotFoundException {

        for (Ticket ticket : tickets) {

            if (ticket.getTicketId().equals(ticketId)) {
                ticket.cancelTicket();
                return;
            }
        }

        throw new TicketNotFoundException(
                "Ticket ID not found: " + ticketId
        );
    }

    /**
     * Displays all tickets currently stored in the system.
     */
    public void viewTickets() {

        if (tickets.isEmpty()) {
            System.out.println("No tickets available.");
            return;
        }

        for (Ticket ticket : tickets) {
            ticket.printTicket();
            System.out.println("-------------------------");
        }
    }
}