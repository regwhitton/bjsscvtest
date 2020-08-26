package regwhitton.bjsscvtest.service.cv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import regwhitton.bjsscvtest.model.Cv;

import javax.validation.Valid;

@Component
@Validated
public class CvService {

    public Cv create(@Valid Cv cv){
        // TODO implement
        return null;
    }
}