package regwhitton.bjsscvtest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import regwhitton.bjsscvtest.model.CreateValidation;
import regwhitton.bjsscvtest.model.Cv;
import regwhitton.bjsscvtest.model.UpdateValidation;
import regwhitton.bjsscvtest.service.NotFoundException;
import regwhitton.bjsscvtest.service.repo.CvRepository;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.util.List;

@Service
@Transactional
@Validated
public class CvService {

    @Autowired
    private CvRepository cvRepository;

    @Validated({CreateValidation.class, Default.class})
    public Cv create(@Valid Cv cv) {
        return cvRepository.save(cv);
    }

    @Validated({UpdateValidation.class, Default.class})
    public Cv update(@Valid Cv cv) {
        return cvRepository.save(cv);
    }

    public Cv fetch(@NotNull Long id) throws NotFoundException {
        return cvRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public List<Cv> fetchAll() {
        return cvRepository.findAll();
    }
}