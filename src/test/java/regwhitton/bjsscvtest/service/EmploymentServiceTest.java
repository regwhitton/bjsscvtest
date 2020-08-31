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
import regwhitton.bjsscvtest.model.Employment;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@ActiveProfiles("test")
@Import(EmploymentService.class)
class EmploymentServiceTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    EmploymentService employmentService;

    @Test
    void shouldThrowNotFound_whenCreateWithUnknownCv() {
        final Long UNKNOWN_CV_ID = 123456789L;
        Employment newEmp = Employment.builder().company("Waitrose").build();

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> employmentService.create(UNKNOWN_CV_ID, newEmp));
    }

    @Test()
    void shouldCreateEmploymentOnDatabase_andReturnWithId() {
        Cv cv = entityManager.persist(buildCv());
        entityManager.flush();
        Employment newEmp = Employment.builder()
                .fromDate(LocalDate.of(1994, 8, 31))
                .company("Waitrose")
                .position("Security Guard")
                .summary("Responsible for patrolling the premises overnight and monitoring CCTV.")
                .build();

        Employment createdEmp = employmentService.create(cv.getId(), newEmp);

        assertThat(createdEmp.getId()).isNotNull();
        assertThat(createdEmp.getCvId()).isEqualTo(cv.getId());
        assertThat(createdEmp.getCompany()).isEqualTo("Waitrose");

        Employment dbEmp = entityManager.find(Employment.class, createdEmp.getId());
        assertThat(dbEmp.getCvId()).isEqualTo(cv.getId());
        assertThat(dbEmp.getCompany()).isEqualTo("Waitrose");
    }

    @Test
    void shouldThrowNotFound_whenUpdatingEmploymentForUnknownCv() {
        final Long UNKNOWN_CV_ID = 123456789L;
        Cv cv = entityManager.persist(buildCv());
        Employment originalEmp = entityManager.persist(buildEmp(cv, "Wiatrose"));
        entityManager.flush();
        Employment toUpdate = originalEmp.toBuilder().company("Waitrose").build();

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> employmentService.update(UNKNOWN_CV_ID, toUpdate));
    }

    @Test()
    void shouldFailToUpdateEmployment_ifVersionOutOfDate() {
        Cv cv = entityManager.persist(buildCv());
        Employment originalEmp = entityManager.persist(buildEmp(cv, "Wiatrose"));
        entityManager.flush();
        Employment toUpdate = originalEmp.toBuilder()
                .company("Waitrose")
                .version(originalEmp.getVersion() - 1)
                .build();

        assertThatExceptionOfType(ObjectOptimisticLockingFailureException.class)
                .isThrownBy(() -> employmentService.update(cv.getId(), toUpdate));
    }

    @Test()
    void shouldUpdateEmploymentOnDatabase_andReturnWithNewVersion() {
        Cv cv = entityManager.persist(buildCv());
        Employment originalEmp = entityManager.persist(buildEmp(cv, "Wiatrose"));
        entityManager.flush();
        Long originalVersion = originalEmp.getVersion();
        Employment toUpdate = originalEmp.toBuilder().company("Waitrose").build();

        Employment updatedEmp = employmentService.update(cv.getId(), toUpdate);
        entityManager.flush();

        assertThat(updatedEmp.getCompany()).isEqualTo("Waitrose");
        assertThat(updatedEmp.getVersion()).isEqualTo(originalVersion + 1);
        Employment dbEmp = entityManager.find(Employment.class, originalEmp.getId());
        assertThat(dbEmp.getCompany()).isEqualTo("Waitrose");
        assertThat(dbEmp.getVersion()).isEqualTo(originalVersion + 1);
    }

    @Test
    void shouldThrowNotFound_whenDeletingEmploymentForUnknownCv() {
        final Long UNKNOWN_CV_ID = 123456789L;
        Cv cv = entityManager.persist(buildCv());
        Employment newEmp = entityManager.persist(buildEmp(cv, "Waitrose"));
        entityManager.flush();

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> employmentService.delete(UNKNOWN_CV_ID, newEmp.getId()));
    }

    @Test
    void shouldThrowNotFound_whenDeletingEmploymentForWrongCv() {
        Cv cv = entityManager.persist(buildCv());
        Employment emp = entityManager.persist(buildEmp(cv, "Waitrose"));
        Cv otherCv = entityManager.persist(buildCv());
        entityManager.flush();

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> employmentService.delete(otherCv.getId(), emp.getId()));
    }

    @Test
    void shouldThrowNotFound_whenDeletingUnknownSkill() {
        final Long UNKNOWN_SKILL_ID = 123456789L;
        Cv cv = entityManager.persist(buildCv());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> employmentService.delete(cv.getId(), UNKNOWN_SKILL_ID));
    }

    @Test
    void shouldDeleteEmployment() {
        Cv cv = entityManager.persist(buildCv());
        Employment emp = entityManager.persist(buildEmp(cv, "Waitrose"));
        entityManager.flush();
        Employment dbEmpBefore = entityManager.find(Employment.class, emp.getId());
        assertThat(dbEmpBefore).isNotNull();

        employmentService.delete(cv.getId(), emp.getId());

        Employment dbEmpAfter = entityManager.find(Employment.class, emp.getId());
        assertThat(dbEmpAfter).isNull();
    }

    @Test
    void shouldThrowNotFound_whenFetchingEmploymentsForUnknownCv() {
        final Long UNKNOWN_CV_ID = 123456789L;

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> employmentService.fetchAll(UNKNOWN_CV_ID));
    }

    @Test
    void shouldFetchEmploymentsForCvId() {
        Cv cv = buildCv();
        Employment emp1 = buildEmp(cv, "Waitrose");
        Employment emp2 = buildEmp(cv, "Asda");
        cv.getEmploymentHistory().add(emp1);
        cv.getEmploymentHistory().add(emp2);
        entityManager.persist(cv);
        entityManager.flush();

        List<Employment> employmentHistory = employmentService.fetchAll(cv.getId());

        assertThat(employmentHistory).hasSize(2);
        assertThat(employmentHistory.get(0).getCompany()).isEqualTo("Waitrose");
        assertThat(employmentHistory.get(1).getCompany()).isEqualTo("Asda");
    }

    private Cv buildCv() {
        return Cv.builder()
                .firstName("Phillip")
                .surname("Carlton")
                .summary("Security Guard")
                .dateOfBirth(LocalDate.of(1973, 12, 31))
                .address(Address.builder()
                        .addressLine1("Flat 1, 53 Trench Street")
                        .postalCode("UI99 5JT")
                        .build())
                .build();
    }

    private Employment buildEmp(Cv cv, String company) {
        return Employment.builder()
                .cv(cv)
                .fromDate(LocalDate.of(1994, 8, 31))
                .company(company)
                .position("Security Guard")
                .summary("Responsible for patrolling the premises overnight and monitoring CCTV.")
                .build();
    }
}
