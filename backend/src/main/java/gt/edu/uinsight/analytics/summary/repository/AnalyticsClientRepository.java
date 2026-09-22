package gt.edu.uinsight.analytics.summary.repository;

import gt.edu.uinsight.analytics.summary.dto.external.CentralTendencyData;
import gt.edu.uinsight.analytics.summary.dto.external.DispersionData;
import gt.edu.uinsight.analytics.summary.dto.external.PositionData;
import gt.edu.uinsight.analytics.summary.dto.external.StudentComparisonData;
import gt.edu.uinsight.analytics.summary.dto.external.TrendData;

public interface AnalyticsClientRepository {
    CentralTendencyData getCentralTendency(Long sectionId);
    PositionData getPosition(Long sectionId);
    DispersionData getDispersion(Long sectionId);
    TrendData getTrend(Long sectionId);
    StudentComparisonData getStudentComparison(Long sectionId);
}
