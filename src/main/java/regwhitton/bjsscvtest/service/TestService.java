package regwhitton.bjsscvtest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.RequestBody;
import regwhitton.bjsscvtest.model.BodyType;
import regwhitton.bjsscvtest.model.ReturnType;
import regwhitton.bjsscvtest.restapi.TestController;

import javax.validation.Valid;
import java.math.BigDecimal;

@Component
public class TestService {

    //private static final Logger LOG = LoggerFactory.getLogger(TestService.class);

    @Transactional(rollbackFor = ServiceException.class)
    public BigDecimal doubleIt(BigDecimal value) throws NotFoundException {
        //LOG.debug("We are in tx " + TransactionSynchronizationManager.isActualTransactionActive());
        return value.multiply(BigDecimal.valueOf(2));
    }
}
