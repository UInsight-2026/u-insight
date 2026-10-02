package gt.edu.uinsight.analytics.summary.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;

import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.repository.AnalyticsClientRepository;

@Service
public class SummaryService {

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

        // 1. Tendencia central
        try {
            var data = CompletableFuture
                    .supplyAsync(() -> analyticsClientRepository.getCentralTendency(sectionId))
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (data == null) unavailableComponents.add("centralTendency");
            else summary.setCentralTendencyData(data);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            unavailableComponents.add("centralTendency");
        } catch (TimeoutException e) {
            // Componente no respondió a tiempo
            unavailableComponents.add("centralTendency");
        } catch (Exception e) {
            unavailableComponents.add("centralTendency");
        }

        // 2. Posición
        try {
            var data = CompletableFuture
                    .supplyAsync(() -> analyticsClientRepository.getPosition(sectionId))
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (data == null) unavailableComponents.add("position");
            else summary.setPositionData(data);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            unavailableComponents.add("position");
        } catch (TimeoutException e) {
            // Componente no respondió a tiempo
            unavailableComponents.add("position");
        } catch (Exception e) {
            unavailableComponents.add("position");
        }

        // 3. Dispersión
        try {
            var data = CompletableFuture
                    .supplyAsync(() -> analyticsClientRepository.getDispersion(sectionId))
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (data == null) unavailableComponents.add("dispersion");
            else summary.setDispersionData(data);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            unavailableComponents.add("dispersion");
        } catch (TimeoutException e) {
            // Componente no respondió a tiempo
            unavailableComponents.add("dispersion");
        } catch (Exception e) {
            unavailableComponents.add("dispersion");
        }

        // 4. Tendencia
        try {
            var data = CompletableFuture
                    .supplyAsync(() -> analyticsClientRepository.getTrend(sectionId))
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (data == null) unavailableComponents.add("trend");
            else summary.setTrendData(data);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            unavailableComponents.add("trend");
        } catch (TimeoutException e) {
            // Componente no respondió a tiempo
            unavailableComponents.add("trend");
        } catch (Exception e) {
            unavailableComponents.add("trend");
        }

        // 5. Comparación de estudiante
        try {
            var data = CompletableFuture
                    .supplyAsync(() -> analyticsClientRepository.getStudentComparison(sectionId))
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (data == null) unavailableComponents.add("studentComparison");
            else summary.setStudentComparisonData(data);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            unavailableComponents.add("studentComparison");
        } catch (TimeoutException e) {
            // Componente no respondió a tiempo
            unavailableComponents.add("studentComparison");
        } catch (Exception e) {
            unavailableComponents.add("studentComparison");
        }

        summary.setUnavailableComponents(unavailableComponents);
        return summary;
    }
}