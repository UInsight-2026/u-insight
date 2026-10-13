package gt.edu.uinsight.analytics.centraltendency.dto.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Respuesta de A5 (EvaluationResponse): elemento de
 * GET /api/v1/sections/{id}/evaluations. B1 solo usa id, para confirmar que
 * el evaluationId solicitado realmente pertenece a la seccion consultada.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EvaluationApiResponse(Long id) {
}
