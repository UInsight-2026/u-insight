package gt.edu.uinsight.analytics.summary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionClassification;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.position.dto.response.SectionPositionResponse;
import gt.edu.uinsight.analytics.repository.AnalyticsDatabaseRepository;
import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.repository.AnalyticsClientRepository;
import gt.edu.uinsight.analytics.trend.dto.response.TrendPoint;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.service.TrendClassification;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SummaryServiceTest {

    private static final Long SECTION_ID = 101L;

    @Test
    @DisplayName("Incluye el conteo de estudiantes en riesgo consultado en la base de datos")
    void getSummary_sectionAnalyticsAvailable_includesRiskCount() {
        SummaryService summaryService = summaryService(new FixtureAnalyticsClientRepository());

        SectionSummary result = summaryService.getSummary(SECTION_ID);

        assertNotNull(result);
        assertEquals(SECTION_ID, result.getSectionId());
        assertNotNull(result.getCentralTendencyData());
        assertNotNull(result.getPositionData());
        assertNotNull(result.getDispersionData());
        assertNotNull(result.getTrendData());
        assertEquals(List.of(), result.getUnavailableComponents());
        assertEquals(3, result.getStudentsAtRisk());
        assertNull(result.getStudentComparisonData());
    }

    @Test
    @DisplayName("Un componente fallido no impide incluir los demás")
    void getSummary_trendFails_returnsPartialSummary() {
        SummaryService summaryService = summaryService(new FixtureAnalyticsClientRepository(false, true));

        SectionSummary result = summaryService.getSummary(SECTION_ID);

        assertNotNull(result.getCentralTendencyData());
        assertNotNull(result.getPositionData());
        assertNotNull(result.getDispersionData());
        assertNull(result.getTrendData());
        assertEquals(List.of("trend"), result.getUnavailableComponents());
    }

    @Test
    @DisplayName("Componentes vacíos o fallidos se reportan sin perder el resumen")
    void getSummary_allComponentsUnavailable_returnsAllUnavailable() {
        SummaryService summaryService = summaryService(new FixtureAnalyticsClientRepository(true, false));

        SectionSummary result = summaryService.getSummary(SECTION_ID);

        assertNotNull(result);
        assertEquals(List.of("centralTendency", "position", "dispersion", "trend"),
                result.getUnavailableComponents());
    }

    @Test
    @DisplayName("Marca el conteo de riesgo como no disponible si falla la consulta a la base")
    void getSummary_riskCountQueryFails_marksRiskCountUnavailable() {
        AnalyticsDatabaseRepository databaseRepository = mock(AnalyticsDatabaseRepository.class);
        when(databaseRepository.countStudentsBelowAverage(SECTION_ID, 60.0))
                .thenThrow(new IllegalStateException("database unavailable"));
        SummaryService summaryService = new SummaryService(
                new FixtureAnalyticsClientRepository(), databaseRepository, 60.0);

        SectionSummary result = summaryService.getSummary(SECTION_ID);

        assertNull(result.getStudentsAtRisk());
        assertEquals(List.of("studentsAtRisk"), result.getUnavailableComponents());
    }

    @Test
    @DisplayName("Inicia todas las consultas antes de esperar resultados")
    void getSummary_runsComponentCallsConcurrently() {
        SummaryService summaryService = new SummaryService(
                new ConcurrentAnalyticsClientRepository(), concurrentRiskRepository(), 60.0);

        SectionSummary result = summaryService.getSummary(SECTION_ID);

        assertNotNull(result.getCentralTendencyData());
        assertNotNull(result.getPositionData());
        assertNotNull(result.getDispersionData());
        assertNotNull(result.getTrendData());
        assertEquals(3, result.getStudentsAtRisk());
        assertEquals(List.of(), result.getUnavailableComponents());
    }

    @Test
    @DisplayName("Incluye componentes que tardan más de 500 ms")
    void getSummary_slowComponent_isStillIncluded() {
        SummaryService summaryService = summaryService(
                new FixtureAnalyticsClientRepository(false, false, 600));

        SectionSummary result = summaryService.getSummary(SECTION_ID);

        assertNotNull(result.getCentralTendencyData());
        assertEquals(List.of(), result.getUnavailableComponents());
    }

    @Test
    @DisplayName("Rechaza identificadores de sección inválidos")
    void getSummary_invalidSectionId_throwsIllegalArgumentException() {
        SummaryService summaryService = summaryService(new FixtureAnalyticsClientRepository());

        assertThrows(IllegalArgumentException.class, () -> summaryService.getSummary(0L));
    }

    private SummaryService summaryService(AnalyticsClientRepository clientRepository) {
        AnalyticsDatabaseRepository databaseRepository = mock(AnalyticsDatabaseRepository.class);
        when(databaseRepository.countStudentsBelowAverage(SECTION_ID, 60.0)).thenReturn(3);
        return new SummaryService(clientRepository, databaseRepository, 60.0);
    }

    private AnalyticsDatabaseRepository concurrentRiskRepository() {
        AnalyticsDatabaseRepository databaseRepository = mock(AnalyticsDatabaseRepository.class);
        when(databaseRepository.countStudentsBelowAverage(SECTION_ID, 60.0)).thenAnswer(invocation -> {
            ConcurrentAnalyticsClientRepository.awaitOtherCalls();
            return 3;
        });
        return databaseRepository;
    }

    private static final class FixtureAnalyticsClientRepository implements AnalyticsClientRepository {

        private final boolean unavailable;
        private final boolean trendFails;
        private final long centralTendencyDelayMillis;

        private FixtureAnalyticsClientRepository() {
            this(false, false, 0);
        }

        private FixtureAnalyticsClientRepository(boolean unavailable, boolean trendFails) {
            this(unavailable, trendFails, 0);
        }

        private FixtureAnalyticsClientRepository(
                boolean unavailable, boolean trendFails, long centralTendencyDelayMillis) {
            this.unavailable = unavailable;
            this.trendFails = trendFails;
            this.centralTendencyDelayMillis = centralTendencyDelayMillis;
        }

        @Override
        public CentralTendencyResponse getCentralTendency(Long sectionId) {
            if (centralTendencyDelayMillis > 0) {
                try {
                    Thread.sleep(centralTendencyDelayMillis);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("La prueba fue interrumpida", exception);
                }
            }
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

    private static final class ConcurrentAnalyticsClientRepository implements AnalyticsClientRepository {

        private static final CountDownLatch allCallsStarted = new CountDownLatch(5);

        @Override
        public CentralTendencyResponse getCentralTendency(Long sectionId) {
            awaitOtherCalls();
            return new CentralTendencyResponse(4, 75.0, 74.0, List.of(75.0));
        }

        @Override
        public SectionPositionResponse getPosition(Long sectionId) {
            awaitOtherCalls();
            return new SectionPositionResponse(sectionId, 4,
                    Map.of("Q1", 68.0, "Q2", 74.0, "Q3", 82.0), Map.of());
        }

        @Override
        public DispersionResponse getDispersion(Long sectionId) {
            awaitOtherCalls();
            return new DispersionResponse(sectionId, null,
                    new BigDecimal("60"), new BigDecimal("90"),
                    new BigDecimal("30"), new BigDecimal("100"),
                    new BigDecimal("10"), DispersionClassification.MODERATE_DISPERSION);
        }

        @Override
        public TrendResponse getTrend(Long sectionId) {
            awaitOtherCalls();
            return new TrendResponse(TrendClassification.STABLE, BigDecimal.ZERO,
                    List.<TrendPoint>of());
        }

        private static void awaitOtherCalls() {
            allCallsStarted.countDown();
            try {
                if (!allCallsStarted.await(1, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Las llamadas no se iniciaron en paralelo");
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("La prueba fue interrumpida", exception);
            }
        }
    }
}
