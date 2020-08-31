package regwhitton.bjsscvtest.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import regwhitton.bjsscvtest.model.Employment;
import regwhitton.bjsscvtest.service.repo.CvRepository;
import regwhitton.bjsscvtest.service.repo.EmploymentRepository;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@WebMvcTest(EmploymentService.class)
@AutoConfigureMockMvc(addFilters = false, webClientEnabled = false, webDriverEnabled = false)
@ActiveProfiles("test")
class EmploymentServiceValidationTest {

    @MockBean
    private CvRepository cvRepository;

    @MockBean
    private EmploymentRepository employmentRepository;

    @Autowired
    private EmploymentService employmentService;

    final private Employment empWithoutIdAndVersion = Employment.builder()
            .fromDate(LocalDate.of(1994, 8, 31))
            .company("Waitrose")
            .position("Security Guard")
            .summary("Responsible for patrolling the premises overnight and monitoring CCTV.")
            .build();

    @Test
    void shouldVerifyEmploymentToCreateHasNoId() {
        Employment empWithId = empWithoutIdAndVersion.toBuilder().id(2222L).build();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> employmentService.create(100L, empWithId))
                .withMessageEndingWith("id: must be null");
    }

    @Test
    void shouldVerifyEmploymentToCreateHasNoVersion() {
        Employment empWithVersion = empWithoutIdAndVersion.toBuilder().version(2L).build();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> employmentService.create(100L, empWithVersion))
                .withMessageEndingWith("version: should not be provided");
    }

    @Test
    void shouldVerifyEmploymentToUpdateHasId() {
        Employment empWithNoId = empWithoutIdAndVersion.toBuilder().version(2L).build();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> employmentService.update(2000L, empWithNoId))
                .withMessageEndingWith("id: must not be null");
    }

    @Test
    void shouldVerifyEmploymentToUpdateHasVersion() {
        Employment empWithNoVersion = empWithoutIdAndVersion.toBuilder().id(2L).build();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> employmentService.update(2000L, empWithNoVersion))
                .withMessageEndingWith("version: must not be null");
    }

    @Test
    void shouldVerifyDefaultValidation() {
        Employment empWithoutCompany = Employment.builder()
                .fromDate(LocalDate.of(1994, 8, 31))
                .position("Security Guard")
                .summary("Responsible for patrolling the premises overnight and monitoring CCTV.")
                .build();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> employmentService.create(2000L, empWithoutCompany))
                .withMessageEndingWith("company: must not be blank");
    }
}
