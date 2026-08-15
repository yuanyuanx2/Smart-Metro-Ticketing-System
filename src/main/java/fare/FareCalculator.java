package fare;

import enums.TicketType;
import model.Route;

/**
 * Defines the contract for calculating metro ticket fares.
 */
public interface FareCalculator {

    // Calculates the fare based on the route and selected ticket type.
    double calculateFare(Route route, TicketType ticketType);
}