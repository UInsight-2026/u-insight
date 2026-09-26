package gt.edu.uinsight.analytics.centraltendency.dto.integration;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Elemento de la respuesta de A6: GET /api/v1/sections/{id}/grades. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GradeApiResponse(
        Long id,
        Long evaluationId,
        Long studentId,
        BigDecimal score,
        String status
) {
}
