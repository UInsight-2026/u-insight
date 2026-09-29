package gt.edu.uinsight.analytics.dispersion.mapper;

import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionClassification;
import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import org.springframework.stereotype.Component;

/**
 * Construye el DispersionResponse a partir de valores ya calculados.
 * Este mapper NO calcula nada (min, varianza, etc. los produce el
 * DispersionCalculator/Service en semanas posteriores); solo arma
 * el objeto de salida para que el controller lo devuelva.
 */
@Component
public class DispersionMapper {

    public DispersionResponse toSectionResponse(Long sectionId,
                                                 Double min,
                                                 Double max,
                                                 Double range,
                                                 Double variance,
                                                 Double standardDeviation,
                                                 DispersionClassification classification) {
        return new DispersionResponse(
                sectionId, null, min, max, range, variance, standardDeviation, classification
        );
    }

    public DispersionResponse toCourseResponse(Long courseId,
                                                Double min,
                                                Double max,
                                                Double range,
                                                Double variance,
                                                Double standardDeviation,
                                                DispersionClassification classification) {
        return new DispersionResponse(
                null, courseId, min, max, range, variance, standardDeviation, classification
        );
    }
}
