package regwhitton.bjsscvtest.restapi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

/**
 * Test controller that throws the injected exception. Used by
 * {@link ExceptionHandlersTest} to test {@link ExceptionHandlers}.
 */
@RestController
@RequestMapping("/entity")
public class ExceptionThrowingController {

    private Throwable throwable;

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @PutMapping("/{id}")
    public void update(
            @PathVariable Long id,
            @Valid @RequestBody Body body)
            throws Throwable {

        if (throwable != null) {
            throw throwable;
        }
    }

    @GetMapping("/{id}")
    public Body get(@PathVariable Long id) throws Throwable {
        if (throwable != null) {
            throw throwable;
        }
        return new Body();
    }

    @Data
    public static class Body {

        @Schema(accessMode = Schema.AccessMode.READ_ONLY)
        Long id;

        @Pattern(regexp = "^$")
        String invalidIfNotBlank;

        String aField;
    }
}