package regwhitton.bjsscvtest.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import regwhitton.bjsscvtest.model.Address;
import regwhitton.bjsscvtest.model.Cv;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@ActiveProfiles("test")
@Import(CvService.class)
class CvServiceTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    CvService cvService;

    @Test()
    void should_createCvOnDatabase_andReturnCvWithId() {
        Cv originalCv = Cv.builder()
                .firstName("Sidney")
                .preferredFirstName("Sid")
                .surname("James")
                .summary("Professional Actor")
                .dateOfBirth(LocalDate.of(1983, 12, 31))
                .build();

        Cv createdCv = cvService.create(originalCv);

        assertThat(createdCv.getId()).isNotNull();
        assertThat(createdCv.getVersion()).isNotNull();
        assertThat(createdCv.getFirstName()).isEqualTo("Sidney");
        assertThat(createdCv.getPreferredFirstName()).isEqualTo("Sid");
        assertThat(createdCv.getSurname()).isEqualTo("James");
        assertThat(createdCv.getSummary()).isEqualTo("Professional Actor");
        assertThat(createdCv.getDateOfBirth()).isEqualTo(LocalDate.of(1983, 12, 31));

        Cv dbCv = entityManager.find(Cv.class, createdCv.getId());
        assertThat(dbCv.getId()).isEqualTo(createdCv.getId());
        assertThat(dbCv.getVersion()).isEqualTo(createdCv.getVersion());
        assertThat(dbCv.getFirstName()).isEqualTo("Sidney");
        assertThat(dbCv.getPreferredFirstName()).isEqualTo("Sid");
        assertThat(dbCv.getSurname()).isEqualTo("James");
        assertThat(dbCv.getSummary()).isEqualTo("Professional Actor");
        assertThat(dbCv.getDateOfBirth()).isEqualTo(LocalDate.of(1983, 12, 31));
    }

    @Test()
    void should_updateCvOnDatabase_andReturnCvWithNewVersion() {
        Cv cv = Cv.builder()
                .firstName("Sidney")
                .preferredFirstName("Sid")
                .surname("James")
                .summary("Professional Actor")
                .dateOfBirth(LocalDate.of(1983, 12, 31))
                .build();
        Cv originalCv = entityManager.persistAndFlush(cv);
        Long originalVersion = originalCv.getVersion();
        Cv toUpdate = originalCv.toBuilder().firstName("George").build();

        Cv updatedCv = cvService.update(toUpdate);
        entityManager.flush();

        assertThat(updatedCv.getFirstName()).isEqualTo("George");
        assertThat(updatedCv.getVersion()).isEqualTo(originalVersion + 1);
        Cv dbCv = entityManager.find(Cv.class, originalCv.getId());
        assertThat(dbCv.getFirstName()).isEqualTo("George");
        assertThat(dbCv.getVersion()).isEqualTo(originalVersion + 1);
    }

    @Test()
    void shouldFailToUpdate_ifVersionOutOfDate() {
        Cv cv = Cv.builder()
                .firstName("Sidney")
                .preferredFirstName("Sid")
                .surname("James")
                .summary("Professional Actor")
                .dateOfBirth(LocalDate.of(1983, 12, 31))
                .build();
        Cv originalCv = entityManager.persistAndFlush(cv);
        Cv toUpdate = originalCv.toBuilder().version(originalCv.getVersion() - 1).build();

        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> cvService.update(toUpdate));
    }

    @Test
    void shouldThrowNotFound_whenFetchingUnknownCv() {
        final Long UNKNOWN_CV_ID = 123456789L;

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> cvService.fetch(UNKNOWN_CV_ID));
    }

    @Test
    void shouldFetchACv() {
        Cv expectedCv = entityManager.persist(Cv.builder()
                .firstName("Sidney")
                .preferredFirstName("Sid")
                .middleNames("Solomon Joel")
                .surname("James")
                .summary("Professional Actor")
                .dateOfBirth(LocalDate.of(1983, 12, 31))
                .address(Address.builder()
                        .addressLine1("3 Swing Back Lane")
                        .addressLine2("Dimley")
                        .city("Hostand")
                        .county("Dinshire")
                        .postalCode("JK34 8PS")
                        .build()
                )
                .build());
        entityManager.flush();

        Cv cv = cvService.fetch(expectedCv.getId());

        assertThat(cv.getId()).isEqualTo(expectedCv.getId());
        assertThat(cv.getVersion()).isEqualTo(expectedCv.getVersion());
        assertThat(cv.getFirstName()).isEqualTo("Sidney");
        assertThat(cv.getPreferredFirstName()).isEqualTo("Sid");
        assertThat(cv.getMiddleNames()).isEqualTo("Solomon Joel");
        assertThat(cv.getSurname()).isEqualTo("James");
        assertThat(cv.getSummary()).isEqualTo("Professional Actor");
        assertThat(cv.getDateOfBirth()).isEqualTo(LocalDate.of(1983, 12, 31));
        assertThat(cv.getAddress().getAddressLine1()).isEqualTo("3 Swing Back Lane");
        assertThat(cv.getAddress().getPostalCode()).isEqualTo("JK34 8PS");
    }

    @Test
    void shouldFetchAllCvs() {
        Cv cv1 = entityManager.persist(Cv.builder()
                .firstName("Sidney")
                .preferredFirstName("Sid")
                .middleNames("Solomon Joel")
                .surname("James")
                .summary("Professional Actor")
                .dateOfBirth(LocalDate.of(1983, 12, 31))
                .address(Address.builder()
                        .addressLine1("3 Swing Back Lane")
                        .addressLine2("Dimley")
                        .city("Hostand")
                        .county("Dinshire")
                        .postalCode("JK34 8PS")
                        .build()
                )
                .build());
        Cv cv2 = entityManager.persist(Cv.builder()
                .firstName("Joseph")
                .preferredFirstName("Joe")
                .middleNames("Douglas")
                .surname("Bloggs")
                .summary("Sanitation Engineer")
                .dateOfBirth(LocalDate.of(1971, 10, 2))
                .address(Address.builder()
                        .addressLine1("11 George Street")
                        .addressLine2("Grimley")
                        .city("Cheam")
                        .county("North Ruddles")
                        .postalCode("LD99 3JJ")
                        .build()
                )
                .build());
        entityManager.flush();

        List<Cv> cvs = cvService.fetchAll();

        assertThat(cvs.get(0)).isEqualTo(cv1);
        assertThat(cvs.get(1)).isEqualTo(cv2);
    }
}
