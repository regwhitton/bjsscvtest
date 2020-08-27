package regwhitton.bjsscvtest.restapi;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
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
import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static javax.servlet.http.HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Controls the presentation of exceptions within HTTP responses.
 */
@RestControllerAdvice
public class ExceptionHandlers {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionHandlers.class);

    @ExceptionHandler
    @ResponseStatus(UNSUPPORTED_MEDIA_TYPE)
    @ApiResponse(responseCode = "" + SC_UNSUPPORTED_MEDIA_TYPE,
            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\n\t\"error\": \"Content type 'image/jpeg' not supported\"\n}"))})
    public ErrorResponse handleException(HttpMediaTypeNotSupportedException mtypeException) {
        LOG.debug("Client sent unsupported media type: " + mtypeException.getMessage());
        return new ErrorResponse(mtypeException.getMessage());
    }

    /**
     * Handle "not found" business logic exception.
     */
    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    @ApiResponse(responseCode = "" + SC_NOT_FOUND,
            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\n\t\"error\": \"Not found\"\n}"))})
    public ErrorResponse handleException(NotFoundException nfe) {
        return new ErrorResponse(nfe.getMessage());
    }

    /**
     * Handle other business logic exceptions.
     */
    @ExceptionHandler
    @ResponseStatus(UNPROCESSABLE_ENTITY)
    @ApiResponse(responseCode = "422", description = "Business rule prevents processing",
            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\n\t\"error\": \"Insufficient funds available\"\n}"))})
    public ErrorResponse handleException(ServiceException se) {
        LOG.debug("Business logic exception: " + se.getMessage());
        return new ErrorResponse(se.getMessage());
    }

    /**
     * Handle exceptions from bad incoming JSON.
     */
    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleException(HttpMessageConversionException hmce) {
        LOG.debug("Bad incoming request: " + hmce.getMessage());
        return new ErrorResponse("Bad incoming request - please check your JSON");
    }

    /**
     * Handle exceptions from bad parameters.
     */
    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @ApiResponse(responseCode = "" + SC_BAD_REQUEST,
            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\n\t\"error\": \"Cannot convert 'id' parameter from value 'XXXX'\"\n}"))})
    public ErrorResponse handleException(MethodArgumentTypeMismatchException matme) {
        LOG.debug("Bad parameter: " + matme.getMessage());
        return new ErrorResponse("Cannot convert '" + matme.getName() + "' parameter from value '" + matme.getValue() + "'");
    }

    /**
     * Handle exceptions from out-of-date entities (version number incorrect).
     */
    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @ApiResponse(responseCode = "" + SC_BAD_REQUEST,
            content = {@Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{\n\t\"error\": \"version number out-of-date\"\n}"))})
    public ErrorResponse handleException(ObjectOptimisticLockingFailureException oolfe) {
        LOG.debug("Stale object - version number out-of-date");
        return new ErrorResponse("version number out-of-date");
    }

    /**
     * Handle exceptions from @{@link Valid} annotations on controllers or services.
     */
    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleException(MethodArgumentNotValidException manve) {
        LOG.debug("Argument validation failed: " + manve.getMessage());
        return new ErrorResponse(format(manve));
    }

    private String format(MethodArgumentNotValidException manve) {
        return manve.getBindingResult().getAllErrors().stream()
                .map(this::format)
                .collect(joining("\n"));
    }

    private String format(ObjectError err) {
        return String.format("%s %s", ((FieldError) err).getField(), err.getDefaultMessage());
    }

    /**
     * Handle all other {@link Throwable}s.
     * Database constraint exceptions (nested within other {@link RuntimeException}s) are handled by returning
     * 400 Bad Request.  All other exceptions are unexpected in terms of business logic, so these
     * are logged as errors and a 500 Internal Server Error response returned.
     */
    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleException(Throwable ex) {
        return findTypeAmongCauses(ConstraintViolationException.class, ex)
                .map(cve -> error(BAD_REQUEST, format(cve)))
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
