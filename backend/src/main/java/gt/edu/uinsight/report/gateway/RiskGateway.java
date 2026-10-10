package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.report.dto.response.RiskSnapshot;

/** Puerto a B7: devuelve un snapshot no nulo incluso si falla la dependencia. */
public interface RiskGateway {
    RiskSnapshot getSectionRisk(Long sectionId);
}
