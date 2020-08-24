package regwhitton.bjsscvtest.restapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import regwhitton.bjsscvtest.service.NotFoundException;
import regwhitton.bjsscvtest.service.ServiceException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path.Node;
import javax.validation.Valid;
import java.util.Optional;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

/**
 * Controls the presentation of exceptions within HTTP responses.
 */
@RestControllerAdvice
public class ExceptionHandlers {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlers.class);

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(HttpMediaTypeNotSupportedException mtypeException) {
        LOG.debug("Media type not supported: " + mtypeException.getMessage());
        return error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, mtypeException.getMessage());
    }

    /**
     * Handle "not found" business logic exceptions.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleException(NotFoundException nfe) {
        LOG.debug("We2 are in tx " + TransactionSynchronizationManager.isActualTransactionActive());
        return error(NOT_FOUND, nfe.getMessage());
    }

    /**
     * Handle other business logic exceptions.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ErrorResponse> handleException(ServiceException se) {
        LOG.debug("Business logic exception: " + se.getMessage());
        LOG.debug("We3 are in tx " + TransactionSynchronizationManager.isActualTransactionActive());
        return error(UNPROCESSABLE_ENTITY, se.getMessage());
    }

    /**
     * Handle exceptions from bad incoming JSON.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleException(HttpMessageConversionException hmce) {
        LOG.debug("Bad incoming request: " + hmce.getMessage());
        return error(BAD_REQUEST, "Bad incoming request - please check your JSON");
    }

    /**
     * Handle exceptions from bad parameters.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentTypeMismatchException matme) {
        LOG.debug("Bad parameter: " + matme.getMessage());
        return error(BAD_REQUEST, "Cannot convert '" + matme.getName() + "' parameter from value '" +
                matme.getValue() + "'");
    }

    /**
     * Handle exceptions from @{@link Valid} annotations on controllers or services
     * by returning 422 Unprocessable Entity.
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException manve) {
        LOG.debug("Argument validation failed: " + manve.getMessage());
        return error(UNPROCESSABLE_ENTITY, "Invalid argument or field");
    }

    /**
     * Handle all other {@link Throwable}s.
     * Database constraint exceptions (nested within other {@link RuntimeException}s) are handled by returning
     * 422 Unprocessable Entity.  All other exceptions are unexpected in terms of business logic, so these
     * are logged and a 500 Internal Server Error response returned.
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleException(Throwable ex) {
        return findTypeAmongCauses(ConstraintViolationException.class, ex)
                .map(cve -> error(UNPROCESSABLE_ENTITY, format(cve)))
                .orElseGet(() -> {
                    LOG.error("Unexpected exception", ex);
                    return error(INTERNAL_SERVER_ERROR, "Internal server error");
                });
    }

    private <E extends Throwable> Optional<E> findTypeAmongCauses(Class<E> throwableType, Throwable ex) {
        if (ex == null) {
            return Optional.empty();
        } else if (throwableType.isInstance(ex)) {
            return Optional.of(throwableType.cast(ex));
        } else {
            return findTypeAmongCauses(throwableType, ex.getCause());
        }
    }

    private String format(ConstraintViolationException cve) {
        return cve.getConstraintViolations().stream()
                .map(this::format)
                .collect(joining("\n"));
    }

    private String format(ConstraintViolation<?> cv) {
        return String.format("%s %s", propertyPath(cv), cv.getMessage());
    }

    private String propertyPath(ConstraintViolation<?> cv) {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (Node n : cv.getPropertyPath()) {
            if (n.getKind() == ElementKind.BEAN || n.getKind() == ElementKind.METHOD) {
                continue;
            }
            if (n.getName() != null) {
                sb.append(sep);
                sb.append(n.getName());
            }
            sep = ".";
        }
        return sb.toString();
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new ErrorResponse(message), httpStatus);
    }
}
