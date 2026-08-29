package model;

import enums.TicketStatus;
import enums.TicketType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {

    private String ticketId;
    private Passenger passenger;
    private Station source;
    private Station destination;
    private TicketType ticketType;
    private TicketStatus status;
    private double fare;

    private LocalDateTime purchaseDateTime;

    /*
     * Bonus feature:
     * Records whether the one-time loyalty
     * discount was used for this Ticket.
     */
    private boolean loyaltyDiscountApplied;

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("hh:mm a");

    /**
     * Original constructor.
     *
     * New standard Tickets automatically receive
     * the current purchase date/time and do not
     * use a loyalty discount.
     */
    public Ticket(
            String ticketId,
            Passenger passenger,
            Station source,
            Station destination,
            TicketType ticketType,
            TicketStatus status,
            double fare) {

        this(
                ticketId,
                passenger,
                source,
                destination,
                ticketType,
                status,
                fare,
                LocalDateTime.now(),
                false
        );
    }

    /**
     * Existing constructor used when restoring
     * older Ticket records from storage.
     *
     * Older records are treated as having no
     * loyalty discount.
     */
    public Ticket(
            String ticketId,
            Passenger passenger,
            Station source,
            Station destination,
            TicketType ticketType,
            TicketStatus status,
            double fare,
            LocalDateTime purchaseDateTime) {

        this(
                ticketId,
                passenger,
                source,
                destination,
                ticketType,
                status,
                fare,
                purchaseDateTime,
                false
        );
    }

    /**
     * Full constructor used for Tickets that may
     * contain loyalty-discount information.
     */
    public Ticket(
            String ticketId,
            Passenger passenger,
            Station source,
            Station destination,
            TicketType ticketType,
            TicketStatus status,
            double fare,
            LocalDateTime purchaseDateTime,
            boolean loyaltyDiscountApplied) {

        this.ticketId =
                ticketId;

        this.passenger =
                passenger;

        this.source =
                source;

        this.destination =
                destination;

        this.ticketType =
                ticketType;

        this.status =
                status;

        this.fare =
                fare;

        if (purchaseDateTime == null) {

            this.purchaseDateTime =
                    LocalDateTime.now();

        } else {

            this.purchaseDateTime =
                    purchaseDateTime;
        }

        this.loyaltyDiscountApplied =
                loyaltyDiscountApplied;
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

    /**
     * Returns true when the one-time
     * loyalty discount was used.
     */
    public boolean isLoyaltyDiscountApplied() {

        return loyaltyDiscountApplied;
    }

    /**
     * Displays Ticket information.
     */
    public void printTicket() {

        System.out.println(
                "Ticket ID      : "
                        + ticketId
        );

        System.out.println(
                "Passenger      : "
                        + passenger.getName()
        );

        System.out.println(
                "Source         : "
                        + source.getName()
        );

        System.out.println(
                "Destination    : "
                        + destination.getName()
        );

        System.out.println(
                "Ticket Type    : "
                        + ticketType
        );

        System.out.println(
                "Status         : "
                        + status
        );

        System.out.printf(
                "Fare           : RM%.2f%n",
                fare
        );

        if (loyaltyDiscountApplied) {

            System.out.println(
                    "Loyalty Reward : 20% Discount Applied"
            );
        }

        System.out.println(
                "Purchase Date  : "
                        + purchaseDateTime.format(
                        DATE_FORMAT
                )
        );

        System.out.println(
                "Purchase Time  : "
                        + purchaseDateTime.format(
                        TIME_FORMAT
                )
        );
    }

    /**
     * Cancels this Ticket.
     */
    public void cancelTicket() {

        status =
                TicketStatus.CANCELLED;
    }
}