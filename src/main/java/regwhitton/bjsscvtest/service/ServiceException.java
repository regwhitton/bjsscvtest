package regwhitton.bjsscvtest.service;

/**
 * Exception throw by services to indicate a business rule has been broken.
 * The message will be meaningful the user.
 */
public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }
}

