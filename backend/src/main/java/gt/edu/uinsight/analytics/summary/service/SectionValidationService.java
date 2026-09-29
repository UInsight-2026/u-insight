package gt.edu.uinsight.analytics.summary.service;

import org.springframework.stereotype.Service;

@Service
public class SectionValidationService {

    public boolean exists(Long sectionId) {
        return sectionId != null && sectionId > 0;
    }
}