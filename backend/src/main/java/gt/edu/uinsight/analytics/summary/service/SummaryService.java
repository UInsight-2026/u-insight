package gt.edu.uinsight.analytics.summary.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.repository.AnalyticsClientRepository;

@Service
public class SummaryService {

    private static final Logger log = LoggerFactory.getLogger(SummaryService.class);
    private static final long TIMEOUT_SECONDS = 3;
    private final AnalyticsClientRepository analyticsClientRepository;

    public SummaryService(AnalyticsClientRepository analyticsClientRepository) {
        this.analyticsClientRepository = analyticsClientRepository;
    }

    public SectionSummary getSummary(Long sectionId) {

        if (sectionId == null || sectionId <= 0) {
            throw new IllegalArgumentException("El sectionId debe ser válido");
        }

        SectionSummary summary = new SectionSummary();
        summary.setSectionId(sectionId);
        List<String> unavailableComponents = new ArrayList<>();

        var centralTendency = getComponent("centralTendency",
                () -> analyticsClientRepository.getCentralTendency(sectionId));
        if (centralTendency == null) unavailableComponents.add("centralTendency");
        else summary.setCentralTendencyData(centralTendency);

        var position = getComponent("position",
                () -> analyticsClientRepository.getPosition(sectionId));
        if (position == null) unavailableComponents.add("position");
        else summary.setPositionData(position);

        var dispersion = getComponent("dispersion",
                () -> analyticsClientRepository.getDispersion(sectionId));
        if (dispersion == null) unavailableComponents.add("dispersion");
        else summary.setDispersionData(dispersion);

        var trend = getComponent("trend",
                () -> analyticsClientRepository.getTrend(sectionId));
        if (trend == null) unavailableComponents.add("trend");
        else summary.setTrendData(trend);

        unavailableComponents.add("studentsAtRisk");
        log.warn("SUMMARY_COMPONENT_UNAVAILABLE sectionId={} component=studentsAtRisk reason=not-integrated",
                sectionId);

        summary.setUnavailableComponents(unavailableComponents);
        return summary;
    }

    private <T> T getComponent(String component, Supplier<T> supplier) {
        try {
            return CompletableFuture.supplyAsync(supplier)
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("SUMMARY_COMPONENT_UNAVAILABLE sectionComponent={} reason=interrupted", component, e);
        } catch (TimeoutException e) {
            log.warn("SUMMARY_COMPONENT_UNAVAILABLE sectionComponent={} reason=timeout", component, e);
        } catch (ExecutionException e) {
            log.warn("SUMMARY_COMPONENT_UNAVAILABLE sectionComponent={} reason=service-error",
                    component, e.getCause());
        }
        return null;
    }
}