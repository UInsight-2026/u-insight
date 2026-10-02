package gt.edu.uinsight.analytics.dispersion.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class DispersionService {

    public Map<String, Object> getSectionDispersion(Long sectionId) {

        Map<String, Object> response = new HashMap<>();

        response.put("sectionId", sectionId);
        response.put("min", 45);
        response.put("max", 98);
        response.put("range", 53);
        response.put("variance", 124.6);
        response.put("standardDeviation", 11.16);
        response.put("classification", "MODERATE_DISPERSION");

        return response;
    }
}
