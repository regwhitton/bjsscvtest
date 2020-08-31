package regwhitton.bjsscvtest.restapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import regwhitton.bjsscvtest.model.Employment;
import regwhitton.bjsscvtest.service.EmploymentService;

import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static regwhitton.bjsscvtest.restapi.UriUtils.newResourceUri;

@RestController()
@RequestMapping(path = "/cv/{cvId}/employment")
public class EmploymentController {

    @Autowired
    private EmploymentService employmentService;

    @PostMapping(path = "/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Add an employment to a CV")
    @ApiResponse(responseCode = "" + SC_CREATED, headers = @Header(name = LOCATION, description = "The URI of the created employment"))
    public ResponseEntity<Employment> add(@PathVariable Long cvId, @RequestBody Employment employment) {
        Employment createdEmployment = employmentService.create(cvId, employment);
        return ResponseEntity
                .created(newResourceUri("/cv/{cvId}/employment/{id}", cvId, createdEmployment.getId()))
                .body(createdEmployment);
    }

    @PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Update an employment")
    @ResponseStatus(OK)
    public Employment update(@PathVariable Long cvId, @PathVariable Long id, @RequestBody Employment employment) {
        employment.setId(id);
        return employmentService.update(cvId, employment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employment from a CV")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long cvId, @PathVariable Long id) {
        employmentService.delete(cvId, id);
    }

    @GetMapping(path = "/", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Fetch all employments on a CV")
    @ResponseStatus(OK)
    public List<Employment> fetchAll(@PathVariable Long cvId) {
        return employmentService.fetchAll(cvId);
    }
}