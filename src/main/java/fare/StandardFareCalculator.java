package fare;

import enums.TicketType;
import model.Route;

/**
 * Calculates the standard fare based on route distance
 * and the selected ticket type.
 */
public class StandardFareCalculator implements FareCalculator {

    @Override
    public double calculateFare(Route route, TicketType ticketType) {

        // Standard fare is RM0.50 per kilometre.
        double baseFare = route.calculateDistance() * 0.50;

        // Adjust the fare according to the selected ticket type.
        switch (ticketType) {
            case SINGLE:
                return baseFare;

            case DAILY:
                return baseFare * 2;

            case MONTHLY:
                return baseFare * 10;

            default:
                throw new IllegalArgumentException("Unsupported ticket type.");
        }
    }
}