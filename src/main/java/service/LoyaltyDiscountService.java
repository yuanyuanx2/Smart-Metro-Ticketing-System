package service;

import enums.TicketStatus;
import model.Passenger;
import model.Ticket;

import java.util.ArrayList;

/**
 * Bonus feature service for the
 * passenger loyalty discount system.
 *
 * For every RM100 of eligible spending,
 * the Passenger earns one 20% discount
 * that is applied to a future purchase.
 */
public class LoyaltyDiscountService {

    private static final double SPENDING_THRESHOLD =
            100.00;

    private static final double DISCOUNT_RATE =
            0.20;

    private final ArrayList<Ticket> tickets;

    /**
     * Uses the application's shared Ticket list
     * so loyalty status always reflects the
     * latest Ticket history.
     */
    public LoyaltyDiscountService(
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
     * Returns the amount of loyalty credit that
     * has not yet been exchanged for rewards.
     *
     * Non-cancelled Tickets contribute their
     * actual amount paid.
     *
     * Each previously used loyalty reward
     * consumes RM100 of loyalty credit.
     */
    public double getLoyaltyCredit(
            Passenger passenger) {

        if (passenger == null) {
            return 0.00;
        }

        double eligibleSpending =
                calculateEligibleSpending(
                        passenger
                );

        int rewardsUsed =
                countRewardsUsed(
                        passenger
                );

        double loyaltyCredit =
                eligibleSpending
                        - rewardsUsed
                        * SPENDING_THRESHOLD;

        /*
         * A later cancellation could reduce
         * eligible spending after a reward has
         * already been legitimately used.
         *
         * Loyalty credit is therefore never
         * allowed to become negative.
         */
        return roundCurrency(
                Math.max(
                        0.00,
                        loyaltyCredit
                )
        );
    }

    /**
     * Returns true when at least RM100 of
     * unused loyalty credit is available.
     */
    public boolean isDiscountAvailable(
            Passenger passenger) {

        return getLoyaltyCredit(
                passenger
        ) >= SPENDING_THRESHOLD;
    }

    /**
     * Calculates the 20% loyalty discount.
     *
     * Returns zero when no reward is currently
     * available.
     */
    public double calculateDiscountAmount(
            Passenger passenger,
            double standardFare) {

        if (standardFare < 0) {

            throw new IllegalArgumentException(
                    "Standard fare cannot be negative."
            );
        }

        if (!isDiscountAvailable(
                passenger
        )) {

            return 0.00;
        }

        return roundCurrency(
                standardFare
                        * DISCOUNT_RATE
        );
    }

    /**
     * Calculates the final amount that the
     * Passenger needs to pay.
     */
    public double calculateFinalFare(
            Passenger passenger,
            double standardFare) {

        double discountAmount =
                calculateDiscountAmount(
                        passenger,
                        standardFare
                );

        return roundCurrency(
                standardFare
                        - discountAmount
        );
    }

    /**
     * Returns spending progress toward
     * another RM100 reward.
     *
     * If a reward is already available,
     * this shows the excess spending that
     * has accumulated toward the following
     * reward.
     */
    public double getProgressTowardNextReward(
            Passenger passenger) {

        double loyaltyCredit =
                getLoyaltyCredit(
                        passenger
                );

        return roundCurrency(
                loyaltyCredit
                        % SPENDING_THRESHOLD
        );
    }

    /**
     * Returns the number of currently
     * available unused rewards.
     *
     * Normally this will be 0 or 1.
     * More than one can occur if a very large
     * purchase crosses multiple RM100 thresholds.
     */
    public int getAvailableRewardCount(
            Passenger passenger) {

        double loyaltyCredit =
                getLoyaltyCredit(
                        passenger
                );

        return (int) Math.floor(
                loyaltyCredit
                        / SPENDING_THRESHOLD
        );
    }

    /**
     * Calculates all non-cancelled spending
     * for the Passenger.
     *
     * ACTIVE and USED Tickets count.
     * CANCELLED Tickets do not contribute.
     */
    private double calculateEligibleSpending(
            Passenger passenger) {

        double total =
                0.00;

        for (Ticket ticket :
                tickets) {

            if (!belongsToPassenger(
                    ticket,
                    passenger
            )) {

                continue;
            }

            if (ticket.getStatus()
                    == TicketStatus.CANCELLED) {

                continue;
            }

            total +=
                    ticket.getFare();
        }

        return roundCurrency(
                total
        );
    }

    /**
     * Counts rewards that have already been
     * consumed by successful Ticket purchases.
     *
     * A cancelled discounted Ticket still
     * counts as a used reward. This prevents
     * repeatedly using a reward by purchasing
     * and then cancelling the Ticket.
     */
    private int countRewardsUsed(
            Passenger passenger) {

        int count =
                0;

        for (Ticket ticket :
                tickets) {

            if (belongsToPassenger(
                    ticket,
                    passenger
            )
                    && ticket
                    .isLoyaltyDiscountApplied()) {

                count++;
            }
        }

        return count;
    }

    /**
     * Compares Passenger IDs rather than
     * depending only on object identity.
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
     * Keeps monetary calculations at
     * two decimal places.
     */
    private double roundCurrency(
            double amount) {

        return Math.round(
                amount * 100.0
        ) / 100.0;
    }
}