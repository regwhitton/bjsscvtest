package regwhitton.bjsscvtest.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import regwhitton.bjsscvtest.model.Address;
import regwhitton.bjsscvtest.model.Cv;
import regwhitton.bjsscvtest.model.Skill;
import regwhitton.bjsscvtest.service.repo.CvRepository;
import regwhitton.bjsscvtest.service.repo.SkillRepository;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@WebMvcTest(SkillService.class)
@AutoConfigureMockMvc(addFilters = false, webClientEnabled = false, webDriverEnabled = false)
@ActiveProfiles("test")
class SkillValidationTest {

    @MockBean
    private CvRepository cvRepository;

    @MockBean
    private SkillRepository skillRepository;

    @Autowired
    private SkillService skillService;

    @Test
    void shouldVerifySkillToCreateHasNoId() {
        Skill skillWithId = Skill.builder()
                .id(123456789L)
                .skill("Planisher")
                .build();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> skillService.create(100L, skillWithId))
                .withMessageEndingWith("id: must be null");
    }

    @Test
    void shouldVerifySkillToCreateIsNotBlank() {
        Skill blankSkill = Skill.builder()
                .skill("")
                .build();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> skillService.create(100L, blankSkill))
                .withMessageEndingWith("skill: must not be blank");
    }
}
