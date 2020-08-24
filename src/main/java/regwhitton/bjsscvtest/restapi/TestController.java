package regwhitton.bjsscvtest.restapi;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import regwhitton.bjsscvtest.model.BodyType;
import regwhitton.bjsscvtest.model.ReturnType;
import regwhitton.bjsscvtest.service.NotFoundException;
import regwhitton.bjsscvtest.service.TestService;

import javax.validation.Valid;
import java.math.BigDecimal;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping(path = "/double", produces = {APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Double a number", notes = "Takes a number as a parameter and doubles it.")
    @ApiResponses(value = {
            @ApiResponse(code = 422, message = "Unprocessable Entity", response = ErrorResponse.class)
    })
    public ReturnType doubleArg(@Param("arg") BigDecimal arg) throws NotFoundException {
        return new ReturnType(testService.doubleIt(arg));
    }

    @PostMapping(path = "/double", consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public ReturnType doubleBody(@Valid @RequestBody BodyType body) throws NotFoundException {
        return new ReturnType(testService.doubleIt(body.getValue()));
    }
}
