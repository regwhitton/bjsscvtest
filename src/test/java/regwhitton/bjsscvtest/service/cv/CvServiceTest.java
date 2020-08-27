package regwhitton.bjsscvtest.service.cv;

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
                .build();

        Cv createdCv = cvService.create(originalCv);

        assertThat(createdCv.getId()).isNotNull();
        assertThat(createdCv.getVersion()).isNotNull();
        Cv expectedCv = originalCv.toBuilder().id(createdCv.getId()).build();
        assertThat(createdCv).isEqualTo(expectedCv);
        Cv dbCv = entityManager.find(Cv.class, createdCv.getId());
        assertThat(dbCv).isEqualTo(expectedCv);
    }

    @Test()
    void should_updateCvOnDatabase_andReturnCvWithNewVersion() {
        Cv cv = Cv.builder()
                .firstName("Sidney")
                .preferredFirstName("Sid")
                .surname("James")
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
                .dateOfBirth(LocalDate.of(1983, 12, 31))
                .build();
        Cv originalCv = entityManager.persistAndFlush(cv);
        Cv toUpdate = originalCv.toBuilder().version(originalCv.getVersion() - 1).build();

        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> cvService.update(toUpdate));
    }

    @Test
    void shouldFetchACv() {
        Cv expectedCv = entityManager.persist(Cv.builder()
                .firstName("Sidney")
                .preferredFirstName("Sid")
                .middleNames("Solomon Joel")
                .surname("James")
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

        assertThat(cv).isEqualTo(expectedCv);
    }

    @Test
    void shouldFetchAllCvs() {
        Cv cv1 = entityManager.persist(Cv.builder()
                .firstName("Sidney")
                .preferredFirstName("Sid")
                .middleNames("Solomon Joel")
                .surname("James")
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
                .middleNames("Fredrick Douglas")
                .surname("Bloggs")
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
