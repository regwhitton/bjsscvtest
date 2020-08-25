package regwhitton.bjsscvtest.service;

/**
 * Entity passed in an argument was not found.
 *
 * @see ServiceException
 */
public class NotFoundException extends ServiceException {

    /**
     * Exception indicating that entity was not found.
     */
    public NotFoundException() {
        super("not found");
    }
}
