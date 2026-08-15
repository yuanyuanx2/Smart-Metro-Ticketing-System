package exception;

/**
 * Thrown when a requested ticket ID cannot be found.
 */
public class TicketNotFoundException extends Exception {

    public TicketNotFoundException(String message) {
        super(message);
    }
}