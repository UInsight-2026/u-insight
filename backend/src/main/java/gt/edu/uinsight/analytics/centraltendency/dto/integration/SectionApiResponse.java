package gt.edu.uinsight.analytics.centraltendency.dto.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Respuesta de A4: GET /api/v1/sections/{id} y elemento de GET /api/v1/sections.
 * El campo status no forma parte del contrato publicado de A4 todavia; se mapea
 * para poder filtrar secciones ACTIVE en cuanto A4 lo exponga.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SectionApiResponse(
        Long id,
        String code,
        Long academicPeriodId,
        Long courseId,
        String status
) {
}
