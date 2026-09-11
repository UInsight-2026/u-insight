package gt.edu.uinsight.analytics.individual.controller;

import gt.edu.uinsight.analytics.individual.dto.response.StudentSummaryResponse;
import gt.edu.uinsight.analytics.individual.service.StudentAnalyticsService;
import gt.edu.uinsight.analytics.individual.dto.response.StudentComparisonResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics/students")
public class StudentAnalyticsController {

    private final StudentAnalyticsService studentAnalyticsService;

    public StudentAnalyticsController(StudentAnalyticsService studentAnalyticsService) {
        this.studentAnalyticsService = studentAnalyticsService;
    }

    @GetMapping("/{id}/summary")
    public StudentSummaryResponse getSummary(@PathVariable Long id) {
        return studentAnalyticsService.getSummary(id);
    }

    @GetMapping("/{id}/comparison")
    public StudentComparisonResponse getComparison(@PathVariable Long id) {
        return studentAnalyticsService.getComparison(id);
    }

}
