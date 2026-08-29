package service;

import enums.TicketStatus;
import enums.TicketType;
import model.Passenger;
import model.Ticket;

import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Bonus feature service that summarizes
 * a Passenger's ticket-purchase history.
 *
 * Travel history is based on non-cancelled
 * Ticket purchases because the current system
 * does not require a separate journey-completion
 * tracking mechanism.
 */
public class TravelHistoryService {

    private final ArrayList<Ticket> tickets;

    public TravelHistoryService(
            ArrayList<Ticket> tickets) {

        if (tickets == null) {

            this.tickets =
                    new ArrayList<>();

        } else {

            this.tickets =
                    tickets;
        }
    }

    /**
     * Displays history for the current month.
     */
    public void showCurrentMonthHistory(
            Passenger passenger) {

        YearMonth currentMonth =
                YearMonth.now();

        showMonthlyHistory(
                passenger,
                currentMonth.getYear(),
                currentMonth.getMonthValue()
        );
    }

    /**
     * Displays history for one selected month.
     */
    public void showMonthlyHistory(
            Passenger passenger,
            int year,
            int month) {

        validatePassenger(
                passenger
        );

        if (month < 1
                || month > 12) {

            throw new IllegalArgumentException(
                    "Month must be between 1 and 12."
            );
        }

        if (year < 1
                || year > 9999) {

            throw new IllegalArgumentException(
                    "Invalid year."
            );
        }

        String monthName =
                Month.of(month)
                        .getDisplayName(
                                TextStyle.FULL,
                                Locale.ENGLISH
                        );

        System.out.println(
                "========== TRAVEL HISTORY =========="
        );

        System.out.println();

        System.out.println(
                "Period          : "
                        + monthName
                        + " "
                        + year
        );

        System.out.println();

        ArrayList<Ticket> matchingTickets =
                getMonthlyTickets(
                        passenger,
                        year,
                        month
                );

        displaySummary(
                matchingTickets
        );
    }

    /**
     * Displays all available history for
     * the Passenger.
     */
    public void showAllTimeHistory(
            Passenger passenger) {

        validatePassenger(
                passenger
        );

        System.out.println(
                "========== TRAVEL HISTORY =========="
        );

        System.out.println();

        System.out.println(
                "Period          : All Time"
        );

        System.out.println();

        ArrayList<Ticket> matchingTickets =
                getPassengerTickets(
                        passenger
                );

        displaySummary(
                matchingTickets
        );
    }

    /**
     * Retrieves one Passenger's Tickets
     * for a specific year and month.
     */
    private ArrayList<Ticket> getMonthlyTickets(
            Passenger passenger,
            int year,
            int month) {

        ArrayList<Ticket> result =
                new ArrayList<>();

        for (Ticket ticket :
                tickets) {

            if (!belongsToPassenger(
                    ticket,
                    passenger
            )) {

                continue;
            }

            if (ticket.getPurchaseDateTime()
                    .getYear() == year
                    && ticket.getPurchaseDateTime()
                    .getMonthValue() == month) {

                result.add(
                        ticket
                );
            }
        }

        return result;
    }

    /**
     * Retrieves all Tickets belonging to
     * one Passenger.
     */
    private ArrayList<Ticket> getPassengerTickets(
            Passenger passenger) {

        ArrayList<Ticket> result =
                new ArrayList<>();

        for (Ticket ticket :
                tickets) {

            if (belongsToPassenger(
                    ticket,
                    passenger
            )) {

                result.add(
                        ticket
                );
            }
        }

        return result;
    }

    /**
     * Displays aggregate travel-history information.
     *
     * ACTIVE and USED Tickets count as valid
     * purchase records.
     *
     * CANCELLED Tickets are shown separately
     * and do not contribute to route counts
     * or total spending.
     */
    private void displaySummary(
            ArrayList<Ticket> matchingTickets) {

        if (matchingTickets.isEmpty()) {

            System.out.println(
                    "No travel history found for this period."
            );

            return;
        }

        int validPurchases =
                0;

        int cancelledPurchases =
                0;

        double totalSpent =
                0.00;

        HashMap<String, Integer> routeCounts =
                new HashMap<>();

        HashMap<TicketType, Integer> ticketTypeCounts =
                new HashMap<>();

        for (Ticket ticket :
                matchingTickets) {

            if (ticket.getStatus()
                    == TicketStatus.CANCELLED) {

                cancelledPurchases++;

                continue;
            }

            validPurchases++;

            totalSpent +=
                    ticket.getFare();

            String routeName =
                    ticket.getSource()
                            .getName()
                            + " -> "
                            + ticket.getDestination()
                            .getName();

            routeCounts.put(
                    routeName,
                    routeCounts.getOrDefault(
                            routeName,
                            0
                    ) + 1
            );

            TicketType ticketType =
                    ticket.getTicketType();

            ticketTypeCounts.put(
                    ticketType,
                    ticketTypeCounts.getOrDefault(
                            ticketType,
                            0
                    ) + 1
            );
        }

        System.out.println(
                "Valid Purchases : "
                        + validPurchases
        );

        System.out.println(
                "Cancelled       : "
                        + cancelledPurchases
        );

        System.out.printf(
                "Total Spent     : RM %.2f%n",
                roundCurrency(
                        totalSpent
                )
        );

        System.out.println();

        if (validPurchases == 0) {

            System.out.println(
                    "No non-cancelled travel records found."
            );

            return;
        }

        displayRouteHistory(
                routeCounts
        );

        System.out.println();

        displayTicketTypeHistory(
                ticketTypeCounts
        );

        System.out.println();

        displayMostFrequentRoute(
                routeCounts
        );
    }

    /**
     * Displays route purchase counts,
     * highest frequency first.
     */
    private void displayRouteHistory(
            HashMap<String, Integer> routeCounts) {

        System.out.println(
                "ROUTE HISTORY"
        );

        System.out.println(
                "----------------------------------------"
        );

        routeCounts.entrySet()
                .stream()
                .sorted(
                        Map.Entry
                                .<String, Integer>comparingByValue()
                                .reversed()
                                .thenComparing(
                                        Map.Entry.comparingByKey()
                                )
                )
                .forEach(
                        entry ->
                                System.out.printf(
                                        "%-30s %d time(s)%n",
                                        entry.getKey(),
                                        entry.getValue()
                                )
                );

        System.out.println(
                "----------------------------------------"
        );
    }

    /**
     * Displays Ticket Type usage.
     */
    private void displayTicketTypeHistory(
            HashMap<TicketType, Integer> ticketTypeCounts) {

        System.out.println(
                "TICKET TYPE HISTORY"
        );

        System.out.println(
                "----------------------------------------"
        );

        for (TicketType type :
                TicketType.values()) {

            int count =
                    ticketTypeCounts.getOrDefault(
                            type,
                            0
                    );

            if (count > 0) {

                System.out.printf(
                        "%-12s : %d%n",
                        type,
                        count
                );
            }
        }
    }

    /**
     * Displays the most frequently
     * purchased route.
     */
    private void displayMostFrequentRoute(
            HashMap<String, Integer> routeCounts) {

        Map.Entry<String, Integer> mostFrequent =
                routeCounts.entrySet()
                        .stream()
                        .max(
                                Comparator
                                        .<Map.Entry<String, Integer>>
                                                comparingInt(
                                                Map.Entry::getValue
                                        )
                                        .thenComparing(
                                                Map.Entry::getKey
                                        )
                        )
                        .orElse(
                                null
                        );

        System.out.println(
                "MOST FREQUENT ROUTE"
        );

        System.out.println(
                "----------------------------------------"
        );

        if (mostFrequent == null) {

            System.out.println(
                    "No route history available."
            );

            return;
        }

        System.out.println(
                mostFrequent.getKey()
        );

        System.out.println(
                "Purchased "
                        + mostFrequent.getValue()
                        + " time(s)"
        );
    }

    /**
     * Compares Passenger IDs so loaded
     * Ticket objects remain compatible.
     */
    private boolean belongsToPassenger(
            Ticket ticket,
            Passenger passenger) {

        if (ticket == null
                || ticket.getPassenger() == null
                || passenger == null) {

            return false;
        }

        return ticket.getPassenger()
                .getUserId()
                .equalsIgnoreCase(
                        passenger.getUserId()
                );
    }

    /**
     * Validates Passenger input.
     */
    private void validatePassenger(
            Passenger passenger) {

        if (passenger == null) {

            throw new IllegalArgumentException(
                    "Passenger cannot be null."
            );
        }
    }

    /**
     * Keeps monetary output at two
     * decimal places.
     */
    private double roundCurrency(
            double amount) {

        return Math.round(
                amount * 100.0
        ) / 100.0;
    }
}