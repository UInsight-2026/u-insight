package gt.edu.uinsight.analytics.summary.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.service.SummaryService;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

   
    @GetMapping("/{sectionId}")
    public SectionSummary getSummary(@PathVariable Long sectionId) {
        return summaryService.getSummary(sectionId);
    }
}
