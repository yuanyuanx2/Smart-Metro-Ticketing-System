package exception;

/**
 * Exception thrown when an error occurs during file processing.
 */
public class FileProcessingException extends Exception {

    public FileProcessingException(String message) {
        super(message);
    }
}