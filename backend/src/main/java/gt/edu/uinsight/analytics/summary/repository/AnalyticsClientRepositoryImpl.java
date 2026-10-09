package gt.edu.uinsight.analytics.summary.repository;

import org.springframework.stereotype.Repository;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.centraltendency.service.CentralTendencyService;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.dispersion.service.DispersionService;
<<<<<<< HEAD
import gt.edu.uinsight.analytics.individual.dto.response.StudentComparisonResponse;
=======
>>>>>>> develop
import gt.edu.uinsight.analytics.position.dto.response.SectionPositionResponse;
import gt.edu.uinsight.analytics.position.service.PositionService;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.service.TrendService;

@Repository
public class AnalyticsClientRepositoryImpl implements AnalyticsClientRepository {

    private final CentralTendencyService centralTendencyService;
    private final PositionService positionService;
    private final DispersionService dispersionService;
    private final TrendService trendService;

    public AnalyticsClientRepositoryImpl(
            CentralTendencyService centralTendencyService,
            PositionService positionService,
            DispersionService dispersionService,
            TrendService trendService) {
        this.centralTendencyService = centralTendencyService;
        this.positionService = positionService;
        this.dispersionService = dispersionService;
        this.trendService = trendService;
    }

    @Override
    public CentralTendencyResponse getCentralTendency(Long sectionId) {
        // B1 real — lee de BD
        // evaluationId null porque B6 consulta toda la sección, no una evaluación específica
        return centralTendencyService.getSectionCentralTendency(sectionId, null);
    }

    @Override
    public SectionPositionResponse getPosition(Long sectionId) {
        return positionService.getSectionPosition(sectionId, java.util.List.of());
    }

    @Override
    public DispersionResponse getDispersion(Long sectionId) {
        return dispersionService.getSectionDispersion(sectionId);
    }

    @Override
    public TrendResponse getTrend(Long sectionId) {
        // B4 real — lee de BD
        return trendService.getTrendBySectionId(sectionId);
    }

<<<<<<< HEAD
    @Override
    public StudentComparisonResponse getStudentComparison(Long sectionId) {
        // B5 solo ofrece comparación por estudiante; sectionId no es un studentId.
        return null;
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
=======
>>>>>>> develop
}