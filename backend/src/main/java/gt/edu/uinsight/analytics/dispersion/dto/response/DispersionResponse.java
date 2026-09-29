package gt.edu.uinsight.analytics.dispersion.dto.response;

/**
 * DTO de respuesta para los endpoints de dispersión.
 *
 * Se usa tanto para /sections/{id}/dispersion como para
 * /courses/{id}/dispersion. Solo uno de los dos identificadores
 * (sectionId o courseId) vendrá poblado según el endpoint que
 * lo genere; el otro queda en null.
 *
 * Nota: aquí solo se define la forma del dato. El cálculo real
 * (min, max, varianza, etc.) lo hace el calculator/service más
 * adelante — este DTO no contiene lógica matemática.
 */
public record DispersionResponse(
        Long sectionId,
        Long courseId,
        Double min,
        Double max,
        Double range,
        Double variance,
        Double standardDeviation,
        DispersionClassification classification
) {
}
