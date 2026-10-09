
package gt.edu.uinsight.analytics.dispersion.dto.response;

import java.math.BigDecimal;

/**
 * DTO de respuesta para los endpoints de dispersión.
 *
 * Se utiliza tanto para el análisis de secciones como de cursos.
 * Solo uno de los identificadores estará informado según
 * el endpoint solicitado.
 *
 * No contiene lógica matemática.
 */
public record DispersionResponse(
        Long sectionId,
        Long courseId,
        BigDecimal min,
        BigDecimal max,
        BigDecimal range,
        BigDecimal variance,
        BigDecimal standardDeviation,
        DispersionClassification classification
) {
}
