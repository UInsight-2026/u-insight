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


        summary.setCentralTendencyData(
            analyticsClientRepository.getCentralTendency(sectionId)
        );

    
        summary.setPositionData(
            analyticsClientRepository.getPosition(sectionId)
        );

       
        summary.setDispersionData(
            analyticsClientRepository.getDispersion(sectionId)
        );

   
        summary.setTrendData(
            analyticsClientRepository.getTrend(sectionId)
        );

     
        summary.setStudentComparisonData(
            analyticsClientRepository.getStudentComparison(sectionId)
        );

        return summary;
    }
}