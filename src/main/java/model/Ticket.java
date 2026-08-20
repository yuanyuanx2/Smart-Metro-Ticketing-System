package model;

import enums.TicketStatus;
import enums.TicketType;

import java.time.LocalDateTime;

public class Ticket {

    private String ticketId;
    private Passenger passenger;
    private Station source;
    private Station destination;
    private TicketType ticketType;
    private TicketStatus status;
    private double fare;

    // Extra field for monthly reporting and future file storage
    private LocalDateTime purchaseDateTime;

    // Original constructor kept so existing code will continue working
    public Ticket(String ticketId,
                  Passenger passenger,
                  Station source,
                  Station destination,
                  TicketType ticketType,
                  TicketStatus status,
                  double fare) {

        this(ticketId, passenger, source, destination,
                ticketType, status, fare, LocalDateTime.now());
    }

    // Additional constructor useful for loading saved tickets and testing reports
    public Ticket(String ticketId,
                  Passenger passenger,
                  Station source,
                  Station destination,
                  TicketType ticketType,
                  TicketStatus status,
                  double fare,
                  LocalDateTime purchaseDateTime) {

        this.ticketId = ticketId;
        this.passenger = passenger;
        this.source = source;
        this.destination = destination;
        this.ticketType = ticketType;
        this.status = status;
        this.fare = fare;

        if (purchaseDateTime == null) {
            this.purchaseDateTime = LocalDateTime.now();
        } else {
            this.purchaseDateTime = purchaseDateTime;
        }
    }

    public String getTicketId() {
        return ticketId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Station getSource() {
        return source;
    }

    public Station getDestination() {
        return destination;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public double getFare() {
        return fare;
    }

    public LocalDateTime getPurchaseDateTime() {
        return purchaseDateTime;
    }

    public void printTicket() {
        System.out.println("Ticket ID: " + ticketId);
        System.out.println("Passenger: " + passenger.getName());
        System.out.println("Source: " + source.getName());
        System.out.println("Destination: " + destination.getName());
        System.out.println("Ticket Type: " + ticketType);
        System.out.println("Status: " + status);
        System.out.printf("Fare: RM%.2f%n", fare);
    }

    public void cancelTicket() {
        status = TicketStatus.CANCELLED;
    }
}