package model;

import enums.TicketStatus;
import enums.TicketType;

/**
 * Represents a metro ticket purchased by a passenger.
 */
public class Ticket {

    private String ticketId;
    private Passenger passenger;
    private Station source;
    private Station destination;
    private TicketType ticketType;
    private TicketStatus status;
    private double fare;

    /**
     * Creates a new ticket with its required information.
     */
    public Ticket(String ticketId,
                  Passenger passenger,
                  Station source,
                  Station destination,
                  TicketType ticketType,
                  TicketStatus status,
                  double fare) {

        this.ticketId = ticketId;
        this.passenger = passenger;
        this.source = source;
        this.destination = destination;
        this.ticketType = ticketType;
        this.status = status;
        this.fare = fare;
    }

    // Returns the ticket ID.
    public String getTicketId() {
        return ticketId;
    }

    // Returns the passenger who owns the ticket.
    public Passenger getPassenger() {
        return passenger;
    }

    // Returns the source station.
    public Station getSource() {
        return source;
    }

    // Returns the destination station.
    public Station getDestination() {
        return destination;
    }

    // Returns the selected ticket type.
    public TicketType getTicketType() {
        return ticketType;
    }

    // Returns the current ticket status.
    public TicketStatus getStatus() {
        return status;
    }

    // Returns the ticket fare.
    public double getFare() {
        return fare;
    }

    /**
     * Displays the ticket information.
     */
    public void printTicket() {
        System.out.println("Ticket ID: " + ticketId);
        System.out.println("Passenger: " + passenger.getName());
        System.out.println("Source: " + source.getName());
        System.out.println("Destination: " + destination.getName());
        System.out.println("Ticket Type: " + ticketType);
        System.out.println("Status: " + status);
        System.out.printf("Fare: RM%.2f%n", fare);
    }

    /**
     * Cancels the ticket by changing its status.
     */
    public void cancelTicket() {
        status = TicketStatus.CANCELLED;
    }
}