package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.report.mock.model.MockAlert;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "c5.alert-source", havingValue = "none", matchIfMissing = true)
public class UnavailableAlertQueryPort implements AlertQueryPort {

    @Override
    public List<MockAlert> findAll() {
        return List.of();
    }

    @Override
    public List<MockAlert> findBySectionId(Long sectionId) {
        return List.of();
    }

    @Override
    public boolean isAvailable() {
        return false;
    }



}