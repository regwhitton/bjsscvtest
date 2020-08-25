package regwhitton.bjsscvtest.restapi;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import regwhitton.bjsscvtest.model.BodyType;
import regwhitton.bjsscvtest.model.ReturnType;
import regwhitton.bjsscvtest.service.TestService;

import javax.validation.Valid;
import java.math.BigDecimal;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping(path = "/double", produces = {APPLICATION_JSON_VALUE})
    @Operation(summary = "Double a number", description = "Takes a number as a parameter and doubles it.")
    public ReturnType doubleArg(@Param("arg") BigDecimal arg) {
        return new ReturnType(testService.doubleIt(arg));
    }

    @PostMapping(path = "/double", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public ReturnType doubleBody(@Valid @RequestBody BodyType body) {
        return new ReturnType(testService.doubleIt(body.getValue()));
    }
}
