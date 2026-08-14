package exception;

/**
 * Custom exception used when a user enters
 * an invalid email or password during login.
 */
public class InvalidLoginException extends Exception {

    // 1. Constructor - pass the error message to the parent Exception class
    public InvalidLoginException(String message) {
        super(message);
    }
}
