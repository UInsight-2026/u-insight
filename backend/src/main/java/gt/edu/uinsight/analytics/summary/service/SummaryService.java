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

        List unavailableComponents = new ArrayList<>();

        // 1. Tendencia central (Manejo de fallo parcial / datos nulos)
        try {
            var data = analyticsClientRepository.getCentralTendency(sectionId);
            if (data == null) {
                unavailableComponents.add("centralTendency");
            } else {
                summary.setCentralTendencyData(data);
            }
        } catch (Exception e) {
            unavailableComponents.add("centralTendency");
        }

        // 2. Posición
        try {
            var data = analyticsClientRepository.getPosition(sectionId);
            if (data == null) {
                unavailableComponents.add("position");
            } else {
                summary.setPositionData(data);
            }
        } catch (Exception e) {
            unavailableComponents.add("position");
        }

        // 3. Dispersión
        try {
            var data = analyticsClientRepository.getDispersion(sectionId);
            if (data == null) {
                unavailableComponents.add("dispersion");
            } else {
                summary.setDispersionData(data);
            }
        } catch (Exception e) {
            unavailableComponents.add("dispersion");
        }

        // 4. Tendencia
        try {
            var data = analyticsClientRepository.getTrend(sectionId);
            if (data == null) {
                unavailableComponents.add("trend");
            } else {
                summary.setTrendData(data);
            }
        } catch (Exception e) {
            unavailableComponents.add("trend");
        }

        // 5. Comparación de estudiante
        try {
            var data = analyticsClientRepository.getStudentComparison(sectionId);
            if (data == null) {
                unavailableComponents.add("studentComparison");
            } else {
                summary.setStudentComparisonData(data);
            }
        } catch (Exception e) {
            unavailableComponents.add("studentComparison");
        }

        summary.setUnavailableComponents(unavailableComponents);

        return summary;
    }
}