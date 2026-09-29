package gt.edu.uinsight.analytics.summary.repository;

import java.util.Map;

import org.springframework.stereotype.Repository;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.centraltendency.service.CentralTendencyService;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionClassification;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.dispersion.service.DispersionService;
import gt.edu.uinsight.analytics.individual.dto.response.StudentComparisonResponse;
import gt.edu.uinsight.analytics.individual.service.StudentAnalyticsService;
import gt.edu.uinsight.analytics.summary.dto.external.PositionData;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.service.TrendService;

@Repository
public class AnalyticsClientRepositoryImpl implements AnalyticsClientRepository {

    private final CentralTendencyService centralTendencyService;
    private final DispersionService dispersionService;
    private final TrendService trendService;
    private final StudentAnalyticsService studentAnalyticsService;

    public AnalyticsClientRepositoryImpl(
            CentralTendencyService centralTendencyService,
            DispersionService dispersionService,
            TrendService trendService,
            StudentAnalyticsService studentAnalyticsService) {
        this.centralTendencyService = centralTendencyService;
        this.dispersionService = dispersionService;
        this.trendService = trendService;
        this.studentAnalyticsService = studentAnalyticsService;
    }

    @Override
    public CentralTendencyResponse getCentralTendency(Long sectionId) {
        // B1 real — lee de BD
        // evaluationId null porque B6 consulta toda la sección, no una evaluación específica
        return centralTendencyService.getSectionCentralTendency(sectionId, null);
    }

    @Override
    public PositionData getPosition(Long sectionId) {
        // TODO: migrar cuando B2 exponga PositionResponse
        PositionData data = new PositionData();
        data.setSectionId(sectionId);
        data.setSampleSize(30);
        return data;
    }

    @Override
    public DispersionResponse getDispersion(Long sectionId) {
        // B3 devuelve Map<String, Object> por ahora — convertimos manualmente
        Map<String, Object> raw = dispersionService.getSectionDispersion(sectionId);
        return new DispersionResponse(
                sectionId,
                null,
                toDouble(raw.get("min")),
                toDouble(raw.get("max")),
                toDouble(raw.get("range")),
                toDouble(raw.get("variance")),
                toDouble(raw.get("standardDeviation")),
                toClassification(raw.get("classification"))
        );
    }

    @Override
    public TrendResponse getTrend(Long sectionId) {
        // B4 real — lee de BD
        return trendService.getTrendBySectionId(sectionId);
    }

    @Override
    public StudentComparisonResponse getStudentComparison(Long sectionId) {
        // Individual devuelve comparación por studentId, no sectionId
        // TODO: cuando B5 exponga comparación por sección, migrar aquí
        return studentAnalyticsService.getComparison(sectionId);
    }

    // --- helpers para convertir el Map de B3 ---

    private Double toDouble(Object value) {
        if (value == null) return null;
        if (value instanceof Number n) return n.doubleValue();
        return Double.parseDouble(value.toString());
    }

    private DispersionClassification toClassification(Object value) {
        if (value == null) return null;
        try {
            return DispersionClassification.valueOf(value.toString());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}