package gt.edu.uinsight.analytics.summary.repository;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentComparisonResponse;
import gt.edu.uinsight.analytics.summary.dto.external.PositionData;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;

public interface AnalyticsClientRepository {
    CentralTendencyResponse getCentralTendency(Long sectionId);
    PositionData getPosition(Long sectionId);           // sigue con mock, B2 no tiene DTO
    DispersionResponse getDispersion(Long sectionId);
    TrendResponse getTrend(Long sectionId);
    StudentComparisonResponse getStudentComparison(Long sectionId);
}