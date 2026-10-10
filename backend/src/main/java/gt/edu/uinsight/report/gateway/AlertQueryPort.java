package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.report.mock.model.MockAlert;
import java.util.List;

/** Puerto de lectura de C3. La implementacion JDBC corresponde a C5/Allan. */
public interface AlertQueryPort {
    List<MockAlert> findAll();
    List<MockAlert> findBySectionId(Long sectionId);
    boolean isAvailable();
}
