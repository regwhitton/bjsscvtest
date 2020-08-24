package regwhitton.bjsscvtest.service;

/**
 * Exception throw by services to indicate a business rule has been broken
 * whose message can be shown to the user.
 *
 * Services will roll back transactions when this exception is thrown.
 */
public class ServiceException extends Exception {

    public ServiceException(String message) {
        super(message);
    }
}

