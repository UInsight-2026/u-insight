package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.report.dto.response.AnalyticsSnapshot;

/** Puerto a B6: devuelve un snapshot no nulo incluso si falla la dependencia. */
public interface AnalyticsGateway {
    AnalyticsSnapshot getSectionAnalytics(Long sectionId);
}
