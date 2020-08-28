package regwhitton.bjsscvtest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import regwhitton.bjsscvtest.model.CreateValidation;
import regwhitton.bjsscvtest.model.Cv;
import regwhitton.bjsscvtest.model.Skill;
import regwhitton.bjsscvtest.service.NotFoundException;
import regwhitton.bjsscvtest.service.CvService;
import regwhitton.bjsscvtest.service.repo.CvRepository;
import regwhitton.bjsscvtest.service.repo.SkillRepository;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.groups.Default;
import java.util.List;

@Service
@Transactional
@Validated
public class SkillService {

    @Autowired
    private CvRepository cvRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Validated({CreateValidation.class, Default.class})
    public Skill create(Long cvId, @Valid Skill skill) throws NotFoundException {
        Cv cv = cvRepository.findById(cvId)
                .orElseThrow(NotFoundException::new);
        cv.getSkills().add(skill);
        skill.setCv(cv);
        return skillRepository.save(skill);
    }

    public void delete(Long cvId, Long skillId) throws NotFoundException {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(NotFoundException::new);
        validateCvId(skill, cvId);
        skillRepository.deleteById(skillId);
    }

    private void validateCvId(Skill skill, Long cvId) throws NotFoundException {
        if (!skill.getCvId().equals(cvId)) {
            throw new NotFoundException();
        }
    }

    public List<Skill> fetchAll(Long cvId) throws NotFoundException {
        Cv cv = cvRepository.findById(cvId)
                .orElseThrow(NotFoundException::new);
        return cv.getSkills();
    }
}