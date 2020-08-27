package regwhitton.bjsscvtest.service.cv;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import regwhitton.bjsscvtest.model.Address;
import regwhitton.bjsscvtest.model.Cv;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@WebMvcTest(CvService.class)
@AutoConfigureMockMvc(addFilters = false, webClientEnabled = false, webDriverEnabled = false)
@ActiveProfiles("test")
class CvServiceValidationTest {

    private final Address anAddress = Address.builder()
            .addressLine1("34 Hilgard Road")
            .postalCode("JK23 7YA")
            .build();

    private final Cv cvWithoutIdAndVersion = Cv.builder()
            .firstName("Sidney")
            .preferredFirstName("Sid")
            .middleNames("Solomon Joel")
            .surname("James")
            .dateOfBirth(LocalDate.of(1992, 2, 23))
            .address(anAddress)
            .build();

    @MockBean
    private CvRepository cvRepository;

    @Autowired
    private CvService cvService;

    @Test
    void shouldVerifyCvToCreateHasNoId() {
        Cv cvWithId = cvWithoutIdAndVersion.toBuilder().id(123456789L).build();
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> cvService.create(cvWithId))
                .withMessageEndingWith("id: must be null");
    }

    @Test
    void shouldVerifyCvToCreateHasNoVersion() {
        Cv cvWithVersion = cvWithoutIdAndVersion.toBuilder().version(1L).build();
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> cvService.create(cvWithVersion))
                .withMessageEndingWith("version: must be null");
    }

    @Test
    void shouldVerifyCvToUpdateHasId() {
        Cv cvWithNoId = cvWithoutIdAndVersion.toBuilder().version(1L).build();
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> cvService.update(cvWithNoId))
                .withMessageEndingWith("id: must not be null");
    }

    @Test
    void shouldVerifyCvToUpdateHasVersion() {
        Cv cvWithNoVersion = cvWithoutIdAndVersion.toBuilder().id(123456789L).build();
        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> cvService.update(cvWithNoVersion))
                .withMessageEndingWith("version: must not be null");
    }

    @Test
    void shouldVerifyDefaultValidation() {
        Cv cvWithoutFirstName = Cv.builder()
                .preferredFirstName("Sid")
                .middleNames("Solomon Joel")
                .surname("James")
                .dateOfBirth(LocalDate.of(1992, 2, 23))
                .address(anAddress)
                .build();

        assertThatExceptionOfType(ConstraintViolationException.class)
                .isThrownBy(() -> cvService.create(cvWithoutFirstName))
                .withMessageEndingWith("firstName: must not be blank");
    }
}
