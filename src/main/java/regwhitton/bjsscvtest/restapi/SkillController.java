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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import regwhitton.bjsscvtest.model.Skill;
import regwhitton.bjsscvtest.service.SkillService;

import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static regwhitton.bjsscvtest.restapi.UriUtils.newResourceUri;

@RestController()
@RequestMapping(path = "/cv/{cvId}/skill")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @PostMapping(path = "/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new skill on a CV")
    @ApiResponse(responseCode = "" + SC_CREATED, headers = @Header(name = LOCATION, description = "The URI of the created skill"))
    public ResponseEntity<Skill> create(@PathVariable Long cvId, @RequestBody Skill skill) {
        Skill createdSkill = skillService.create(cvId, skill);
        return ResponseEntity
                .created(newResourceUri("/cv/{cvId}/skill/{id}", cvId, createdSkill.getId()))
                .body(createdSkill);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a skill from a CV")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long cvId, @PathVariable Long id) {
        skillService.delete(cvId, id);
    }

    @GetMapping(path = "/", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Fetch all skills on a CV")
    @ResponseStatus(OK)
    public List<Skill> fetchAll(@PathVariable Long cvId) {
        return skillService.fetchAll(cvId);
    }
}