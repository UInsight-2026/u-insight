package gt.edu.uinsight.analytics.summary.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.position.dto.response.SectionPositionResponse;
import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.repository.AnalyticsClientRepository;
import gt.edu.uinsight.analytics.repository.AnalyticsDatabaseRepository;
import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;

@Service
public class SummaryService {

    private static final Logger log = LoggerFactory.getLogger(SummaryService.class);
    private static final long SUMMARY_TIMEOUT_MILLIS = 500;
    private final AnalyticsClientRepository analyticsClientRepository;
    private final AnalyticsDatabaseRepository databaseRepository;
    private final double studentsAtRiskThreshold;

    public SummaryService(
            AnalyticsClientRepository analyticsClientRepository,
            AnalyticsDatabaseRepository databaseRepository,
            @Value("${uinsight.analytics.students-at-risk-threshold:60.0}") double studentsAtRiskThreshold) {
        this.analyticsClientRepository = analyticsClientRepository;
        this.databaseRepository = databaseRepository;
        this.studentsAtRiskThreshold = studentsAtRiskThreshold;
    }

    public SectionSummary getSummary(Long sectionId) {

        if (sectionId == null || sectionId <= 0) {
            throw new IllegalArgumentException("El sectionId debe ser válido");
        }

        SectionSummary summary = new SectionSummary();
        summary.setSectionId(sectionId);
        List<String> unavailableComponents = new ArrayList<>();

        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(SUMMARY_TIMEOUT_MILLIS);

        CompletableFuture<CentralTendencyResponse> centralTendencyFuture =
                getComponent(() -> analyticsClientRepository.getCentralTendency(sectionId));
        CompletableFuture<SectionPositionResponse> positionFuture =
                getComponent(() -> analyticsClientRepository.getPosition(sectionId));
        CompletableFuture<DispersionResponse> dispersionFuture =
                getComponent(() -> analyticsClientRepository.getDispersion(sectionId));
        CompletableFuture<TrendResponse> trendFuture =
                getComponent(() -> analyticsClientRepository.getTrend(sectionId));
        CompletableFuture<Integer> studentsAtRiskFuture =
                getComponent(() -> databaseRepository.countStudentsBelowAverage(sectionId, studentsAtRiskThreshold));

        setComponentOrMarkUnavailable("centralTendency", centralTendencyFuture, deadline, sectionId,
                unavailableComponents, summary::setCentralTendencyData);
        setComponentOrMarkUnavailable("position", positionFuture, deadline, sectionId,
                unavailableComponents, summary::setPositionData);
        setComponentOrMarkUnavailable("dispersion", dispersionFuture, deadline, sectionId,
                unavailableComponents, summary::setDispersionData);
        setComponentOrMarkUnavailable("trend", trendFuture, deadline, sectionId,
                unavailableComponents, summary::setTrendData);
        setComponentOrMarkUnavailable("studentsAtRisk", studentsAtRiskFuture, deadline, sectionId,
                unavailableComponents, summary::setStudentsAtRisk);

        summary.setUnavailableComponents(unavailableComponents);
        return summary;
    }

    private <T> CompletableFuture<T> getComponent(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier);
    }

    private <T> void setComponentOrMarkUnavailable(
            String component,
            CompletableFuture<T> future,
            long deadline,
            Long sectionId,
            List<String> unavailableComponents,
            Consumer<T> setter) {
        T result = awaitComponent(component, future, deadline, sectionId);
        if (result == null) {
            unavailableComponents.add(component);
        } else {
            setter.accept(result);
        }
    }

    private <T> T awaitComponent(
            String component,
            CompletableFuture<T> future,
            long deadline,
            Long sectionId) {
        try {
            long remainingNanos = Math.max(0, deadline - System.nanoTime());
            return future.get(remainingNanos, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("SUMMARY_COMPONENT_UNAVAILABLE sectionId={} component={} reason=interrupted",
                    sectionId, component, e);
        } catch (TimeoutException e) {
            log.warn("SUMMARY_COMPONENT_UNAVAILABLE sectionId={} component={} reason=timeout",
                    sectionId, component, e);
        } catch (ExecutionException e) {
            log.warn("SUMMARY_COMPONENT_UNAVAILABLE sectionId={} component={} reason=service-error",
                    sectionId, component, e.getCause());
        }
        return null;
    }
}