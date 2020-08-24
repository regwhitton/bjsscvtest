package regwhitton.bjsscvtest.service;

/**
 * Entity passed in an argument was not found.
 *
 * @see ServiceException
 */
public class NotFoundException extends ServiceException {

    /**
     * Exception indicating what was not found.
     *
     * @param entityType the type of entity that was not found.
     * @param entityId the id of the entity that was not found.
     */
    public NotFoundException(String entityType, Long entityId) {
        super(String.format("%s %d not found", entityType, entityId));
    }
}
