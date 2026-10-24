package gt.edu.uinsight.analytics.centraltendency.dto.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Respuesta de A4 (SectionResponseDTO): GET /api/v1/sections/{id} y elemento
 * de GET /api/v1/sections. Contrato confirmado en develop (section.controller
 * .SectionController): id, academicPeriodId, courseId, teacherId,
 * sectionCode, status. B1 solo usa id, academicPeriodId, courseId y status.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SectionApiResponse(
        Long id,
        String sectionCode,
        Long academicPeriodId,
        Long courseId,
        String status
) {
}
