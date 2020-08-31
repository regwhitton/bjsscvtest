package regwhitton.bjsscvtest.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import regwhitton.bjsscvtest.model.Address;
import regwhitton.bjsscvtest.model.Cv;
import regwhitton.bjsscvtest.model.Skill;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DataJpaTest
@ActiveProfiles("test")
@Import(SkillService.class)
class SkillServiceTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    SkillService skillService;

    @Test
    void shouldThrowNotFound_whenCreateWithUnknownCv() {
        final Long UNKNOWN_CV_ID = 123456789L;
        Skill newSkill = Skill.builder().skill("Navigation Certificate").build();

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> skillService.create(UNKNOWN_CV_ID, newSkill));
    }

    @Test()
    void should_createSkillOnDatabase_andReturnSkillWithId() {
        Cv cv = entityManager.persist(buildCv());
        entityManager.flush();
        Skill newSkill = Skill.builder().skill("Navigation Certificate").build();

        Skill createdSkill = skillService.create(cv.getId(), newSkill);
        entityManager.flush();

        assertThat(createdSkill.getId()).isNotNull();
        assertThat(createdSkill.getCvId()).isEqualTo(cv.getId());
        assertThat(createdSkill.getSkill()).isEqualTo("Navigation Certificate");

        Skill dbSkill = entityManager.find(Skill.class, createdSkill.getId());
        assertThat(dbSkill.getCvId()).isEqualTo(cv.getId());
        assertThat(dbSkill.getSkill()).isEqualTo("Navigation Certificate");
    }

    @Test
    void shouldThrowNotFound_whenDeletingSkillForUnknownCv() {
        final Long UNKNOWN_CV_ID = 123456789L;
        Cv cv = entityManager.persist(buildCv());
        Skill skill = entityManager.persist(buildSkill(cv, "Baker"));
        entityManager.flush();

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> skillService.delete(UNKNOWN_CV_ID, skill.getId()));
    }

    @Test
    void shouldThrowNotFound_whenDeletingSkillForWrongCv() {
        Cv cv = entityManager.persist(buildCv());
        Skill skill = entityManager.persist(buildSkill(cv, "Baker"));
        Cv otherCv = entityManager.persist(buildCv());
        entityManager.flush();

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> skillService.delete(otherCv.getId(), skill.getId()));
    }

    @Test
    void shouldThrowNotFound_whenDeletingUnknownSkill() {
        final Long UNKNOWN_SKILL_ID = 123456789L;
        Cv cv = entityManager.persist(buildCv());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> skillService.delete(cv.getId(), UNKNOWN_SKILL_ID));
    }

    @Test
    void shouldDeleteSkill() {
        Cv cv = entityManager.persist(buildCv());
        Skill skill = entityManager.persist(buildSkill(cv, "Baker"));
        entityManager.flush();
        Skill dbSkillBefore = entityManager.find(Skill.class, skill.getId());
        assertThat(dbSkillBefore).isNotNull();

        skillService.delete(cv.getId(), skill.getId());

        Skill dbSkillAfter = entityManager.find(Skill.class, skill.getId());
        assertThat(dbSkillAfter).isNull();
    }

    @Test
    void shouldThrowNotFound_whenFetchingSkillsForUnknownCv() {
        final Long UNKNOWN_CV_ID = 123456789L;

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> skillService.fetchAll(UNKNOWN_CV_ID));
    }

    @Test
    void shouldFetchSkillsForCvId() {
        Cv cv = buildCv();
        Skill skill1 = buildSkill(cv, "Baker");
        Skill skill2 = buildSkill(cv, "Butcher");
        cv.getSkills().add(skill1);
        cv.getSkills().add(skill2);
        entityManager.persist(cv);
        entityManager.flush();

        List<Skill> skills = skillService.fetchAll(cv.getId());

        assertThat(skills).hasSize(2);
        assertThat(skills.get(0).getSkill()).isEqualTo("Baker");
        assertThat(skills.get(1).getSkill()).isEqualTo("Butcher");
    }

    private Cv buildCv() {
        return Cv.builder()
                .firstName("James")
                .surname("Kirk")
                .summary("Starship Captain")
                .dateOfBirth(LocalDate.of(1943, 12, 31))
                .address(Address.builder()
                        .addressLine1("33 Hard Street")
                        .postalCode("TE99 3JJ")
                        .build())
                .build();
    }

    private Skill buildSkill(Cv cv, String skillName) {
        return Skill.builder().cv(cv).skill(skillName).build();
    }
}
