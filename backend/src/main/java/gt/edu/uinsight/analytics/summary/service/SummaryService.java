package gt.edu.uinsight.analytics.summary.service;

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

        SectionSummary summary = new SectionSummary();

        summary.setSectionId(sectionId);

        // Obtener información de Central Tendency
        summary.setCentralTendencyData(
            analyticsClientRepository.getCentralTendency(sectionId)
        );

        // Obtener información de Position
        summary.setPositionData(
            analyticsClientRepository.getPosition(sectionId)
        );

        // Obtener información de Dispersion
        summary.setDispersionData(
            analyticsClientRepository.getDispersion(sectionId)
        );

        // Obtener información de Trend
        summary.setTrendData(
            analyticsClientRepository.getTrend(sectionId)
        );

        // Obtener información de Student Comparison
        summary.setStudentComparisonData(
            analyticsClientRepository.getStudentComparison(sectionId)
        );

        return summary;
    }
}