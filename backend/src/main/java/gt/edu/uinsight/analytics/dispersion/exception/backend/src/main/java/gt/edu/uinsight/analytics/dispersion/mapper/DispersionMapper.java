package gt.edu.uinsight.analytics.dispersion.mapper;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionClassification;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;

@Component
public class DispersionMapper {

    public DispersionResponse toSectionResponse(
            Long sectionId,
            BigDecimal min,
            BigDecimal max,
            BigDecimal range,
            BigDecimal variance,
            BigDecimal standardDeviation,
            DispersionClassification classification) {

        return new DispersionResponse(
                sectionId,
                null,
                min,
                max,
                range,
                variance,
                standardDeviation,
                classification
        );
    }

    public DispersionResponse toCourseResponse(
            Long courseId,
            BigDecimal min,
            BigDecimal max,
            BigDecimal range,
            BigDecimal variance,
            BigDecimal standardDeviation,
            DispersionClassification classification) {

        return new DispersionResponse(
                null,
                courseId,
                min,
                max,
                range,
                variance,
                standardDeviation,
                classification
        );
    }
}
