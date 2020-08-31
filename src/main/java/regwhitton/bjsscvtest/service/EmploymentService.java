package regwhitton.bjsscvtest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import regwhitton.bjsscvtest.model.CreateValidation;
import regwhitton.bjsscvtest.model.Cv;
import regwhitton.bjsscvtest.model.Employment;
import regwhitton.bjsscvtest.model.UpdateValidation;
import regwhitton.bjsscvtest.service.repo.CvRepository;
import regwhitton.bjsscvtest.service.repo.EmploymentRepository;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.util.List;

@Service
@Transactional
@Validated
public class EmploymentService {

    @Autowired
    private CvRepository cvRepository;

    @Autowired
    private EmploymentRepository employmentRepository;

    @Validated({CreateValidation.class, Default.class})
    public Employment create(@NotNull Long cvId, @Valid Employment employment) throws NotFoundException {
        Cv cv = cvRepository.findById(cvId)
                .orElseThrow(NotFoundException::new);
        employment.setCv(cv);
        return employmentRepository.save(employment);
    }

    @Validated({UpdateValidation.class, Default.class})
    public Employment update(@NotNull Long cvId, @Valid Employment employment) throws NotFoundException {
        Employment dbEmp = employmentRepository.findById(employment.getId())
                .orElseThrow(NotFoundException::new);
        validateCv(dbEmp.getCvId(), cvId);

        employment.setCv(dbEmp.getCv());
        return employmentRepository.save(employment);
    }

    public void delete(Long cvId, Long employmentId) throws NotFoundException {
        Employment employment = employmentRepository.findById(employmentId)
                .orElseThrow(NotFoundException::new);
        validateCv(employment.getCvId(), cvId);
        employmentRepository.deleteById(employmentId);
    }

    private void validateCv(Long cvId1, Long cvId2) throws NotFoundException {
        if (!cvId1.equals(cvId2)) {
            throw new NotFoundException();
        }
    }

    public List<Employment> fetchAll(Long cvId) throws NotFoundException {
        Cv cv = cvRepository.findById(cvId)
                .orElseThrow(NotFoundException::new);
        return cv.getEmploymentHistory();
    }
}