package gt.edu.uinsight.analytics.summary.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.repository.AnalyticsClientRepository;

@Service
public class SummaryService {

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

        // Tendencia central
        try {
            summary.setCentralTendencyData(
                analyticsClientRepository.getCentralTendency(sectionId)
            );
        } catch (Exception e) {
            unavailableComponents.add("centralTendency");
        }

        // Posición
        try {
            summary.setPositionData(
                analyticsClientRepository.getPosition(sectionId)
            );
        } catch (Exception e) {
            unavailableComponents.add("position");
        }

        // Dispersión
        try {
            summary.setDispersionData(
                analyticsClientRepository.getDispersion(sectionId)
            );
        } catch (Exception e) {
            unavailableComponents.add("dispersion");
        }

        // Tendencia
        try {
            summary.setTrendData(
                analyticsClientRepository.getTrend(sectionId)
            );
        } catch (Exception e) {
            unavailableComponents.add("trend");
        }

        // Comparación de estudiante
        try {
            summary.setStudentComparisonData(
                analyticsClientRepository.getStudentComparison(sectionId)
            );
        } catch (Exception e) {
            unavailableComponents.add("studentComparison");
        }

        summary.setUnavailableComponents(unavailableComponents);

        return summary;
    }
}


