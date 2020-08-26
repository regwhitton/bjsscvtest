package regwhitton.bjsscvtest.restapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import regwhitton.bjsscvtest.model.Cv;
import regwhitton.bjsscvtest.service.cv.CvService;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static regwhitton.bjsscvtest.restapi.UriUtils.newResourceUri;

@RestController()
@RequestMapping(path = "/cv", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class CvController {

    @Autowired
    private CvService cvService;

    @PostMapping
    @ResponseStatus(OK)
    @Operation(summary = "Create a new CV")
    @ApiResponse(responseCode = "" + SC_CREATED, headers = @Header(name = LOCATION, description = "The URI of the created CV"))
    public ResponseEntity<Cv> createCv(@RequestBody Cv cv) {
        Cv createdCv = cvService.create(cv);
        return ResponseEntity
                .created(newResourceUri("/cv/{id}", createdCv.getId()))
                .body(createdCv);
    }
}