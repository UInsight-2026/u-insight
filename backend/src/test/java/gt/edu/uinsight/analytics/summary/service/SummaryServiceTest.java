package gt.edu.uinsight.analytics.summary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionClassification;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.position.dto.response.SectionPositionResponse;
import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.repository.AnalyticsClientRepository;
import gt.edu.uinsight.analytics.trend.dto.response.TrendPoint;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.service.TrendClassification;

class SummaryServiceTest {

    private static final Long SECTION_ID = 101L;

    @Test
    @DisplayName("Retorna analíticas disponibles y reporta el conteo de riesgo aún no integrado")
    void getSummary_sectionAnalyticsAvailable_reportsRiskCountAsUnavailable() {
        SummaryService summaryService = new SummaryService(new FixtureAnalyticsClientRepository());

        SectionSummary result = summaryService.getSummary(SECTION_ID);

        assertNotNull(result);
        assertEquals(SECTION_ID, result.getSectionId());
        assertNotNull(result.getCentralTendencyData());
        assertNotNull(result.getPositionData());
        assertNotNull(result.getDispersionData());
        assertNotNull(result.getTrendData());
        assertEquals(List.of("studentsAtRisk"), result.getUnavailableComponents());
        assertNull(result.getStudentsAtRisk());
        assertNull(result.getStudentComparisonData());
    }

    @Test
    @DisplayName("Un componente fallido no impide incluir los demás")
    void getSummary_trendFails_returnsPartialSummary() {
        SummaryService summaryService = new SummaryService(
                new FixtureAnalyticsClientRepository(false, true));

        SectionSummary result = summaryService.getSummary(SECTION_ID);

        assertNotNull(result.getCentralTendencyData());
        assertNotNull(result.getPositionData());
        assertNotNull(result.getDispersionData());
        assertNull(result.getTrendData());
        assertEquals(List.of("trend", "studentsAtRisk"), result.getUnavailableComponents());
    }

    @Test
    @DisplayName("Componentes vacíos o fallidos se reportan sin perder el resumen")
    void getSummary_allComponentsUnavailable_returnsAllUnavailable() {
        SummaryService summaryService = new SummaryService(
                new FixtureAnalyticsClientRepository(true, false));

        SectionSummary result = summaryService.getSummary(SECTION_ID);

        assertNotNull(result);
        assertEquals(List.of("centralTendency", "position", "dispersion", "trend", "studentsAtRisk"),
                result.getUnavailableComponents());
    }

    @Test
    @DisplayName("Rechaza identificadores de sección inválidos")
    void getSummary_invalidSectionId_throwsIllegalArgumentException() {
        SummaryService summaryService = new SummaryService(new FixtureAnalyticsClientRepository());

        assertThrows(IllegalArgumentException.class, () -> summaryService.getSummary(0L));
    }

    private static final class FixtureAnalyticsClientRepository implements AnalyticsClientRepository {

        private final boolean unavailable;
        private final boolean trendFails;

        private FixtureAnalyticsClientRepository() {
            this(false, false);
        }

        private FixtureAnalyticsClientRepository(boolean unavailable, boolean trendFails) {
            this.unavailable = unavailable;
            this.trendFails = trendFails;
        }

        @Override
        public CentralTendencyResponse getCentralTendency(Long sectionId) {
            return unavailable ? null
                    : new CentralTendencyResponse(4, 75.0, 74.0, List.of(75.0));
        }

        @Override
        public SectionPositionResponse getPosition(Long sectionId) {
            return unavailable ? null : new SectionPositionResponse(
                    sectionId, 4,
                    Map.of("Q1", 68.0, "Q2", 74.0, "Q3", 82.0),
                    Map.of());
        }

        @Override
        public DispersionResponse getDispersion(Long sectionId) {
            if (unavailable) {
                throw new IllegalStateException("No se pudo calcular la dispersión");
            }
            return new DispersionResponse(sectionId, null,
                    new BigDecimal("60"), new BigDecimal("90"),
                    new BigDecimal("30"), new BigDecimal("100"),
                    new BigDecimal("10"), DispersionClassification.MODERATE_DISPERSION);
        }

        @Override
        public TrendResponse getTrend(Long sectionId) {
            if (unavailable || trendFails) {
                throw new IllegalStateException("No se pudo calcular la tendencia");
            }
            return new TrendResponse(TrendClassification.STABLE, BigDecimal.ZERO,
                    List.<TrendPoint>of());
        }
    }
}