package regwhitton.bjsscvtest.service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import regwhitton.bjsscvtest.model.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}