package gt.edu.uinsight.analytics.summary.repository;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentComparisonResponse;
import gt.edu.uinsight.analytics.position.dto.response.SectionPositionResponse;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;

public interface AnalyticsClientRepository {
    CentralTendencyResponse getCentralTendency(Long sectionId);
    SectionPositionResponse getPosition(Long sectionId);
    DispersionResponse getDispersion(Long sectionId);
    TrendResponse getTrend(Long sectionId);
    StudentComparisonResponse getStudentComparison(Long sectionId);
}