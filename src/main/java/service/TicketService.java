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
     * Creates a ticket service with an empty ticket list.
     */
    public TicketService(
            FareCalculator fareCalculator) {

        this(
                fareCalculator,
                new ArrayList<>()
        );
    }

    /**
     * Creates a ticket service using an existing ticket list.
     *
     * This constructor is used when restoring tickets
     * from TXT storage so the same collection can be
     * shared throughout the application.
     */
    public TicketService(
            FareCalculator fareCalculator,
            ArrayList<Ticket> tickets) {

        this.fareCalculator =
                fareCalculator;

        if (tickets == null) {

            this.tickets =
                    new ArrayList<>();

        } else {

            this.tickets =
                    tickets;
        }
    }

    /**
     * Lecturer-required standard ticket purchase method.
     *
     * The normal fare is calculated using
     * FareCalculator and no loyalty discount is applied.
     */
    public Ticket buyTicket(
            Passenger passenger,
            Route route,
            TicketType type) {

        double fare =
                fareCalculator.calculateFare(
                        route,
                        type
                );

        return buyTicket(
                passenger,
                route,
                type,
                fare,
                false
        );
    }

    /**
     * Bonus-feature overload.
     *
     * Creates a Ticket using the actual final fare
     * that was successfully paid by the Passenger.
     *
     * This allows a loyalty-discounted fare to be
     * stored correctly without changing the lecturer's
     * required FareCalculator interface.
     */
    public Ticket buyTicket(
            Passenger passenger,
            Route route,
            TicketType type,
            double finalFare,
            boolean loyaltyDiscountApplied) {

        if (passenger == null) {

            throw new IllegalArgumentException(
                    "Passenger cannot be null."
            );
        }

        if (route == null) {

            throw new IllegalArgumentException(
                    "Route cannot be null."
            );
        }

        if (type == null) {

            throw new IllegalArgumentException(
                    "Ticket type cannot be null."
            );
        }

        if (finalFare < 0) {

            throw new IllegalArgumentException(
                    "Ticket fare cannot be negative."
            );
        }

        String ticketId =
                String.format(
                        "TKT%03d",
                        tickets.size() + 1
                );

        Ticket ticket =
                new Ticket(
                        ticketId,
                        passenger,
                        route.getSource(),
                        route.getDestination(),
                        type,
                        TicketStatus.ACTIVE,
                        finalFare,
                        null,
                        loyaltyDiscountApplied
                );

        /*
         * Passenger.buyTicket() performs the
         * wallet-balance check and deducts the
         * Ticket's actual final fare.
         */
        passenger.buyTicket(
                ticket
        );

        tickets.add(
                ticket
        );

        return ticket;
    }

    /**
     * Cancels a ticket using its ticket ID.
     */
    public void cancelTicket(
            String ticketId)
            throws TicketNotFoundException {

        for (Ticket ticket :
                tickets) {

            if (ticket.getTicketId()
                    .equals(ticketId)) {

                ticket.cancelTicket();

                return;
            }
        }

        throw new TicketNotFoundException(
                "Ticket ID not found: "
                        + ticketId
        );
    }

    /**
     * Displays all tickets currently stored
     * in the system.
     */
    public void viewTickets() {

        if (tickets.isEmpty()) {

            System.out.println(
                    "No tickets available."
            );

            return;
        }

        for (Ticket ticket :
                tickets) {

            ticket.printTicket();

            System.out.println(
                    "-------------------------"
            );
        }
    }
}