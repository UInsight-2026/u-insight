package gt.edu.uinsight.alert.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.uinsight.alert.dto.response.RiskEvaluationResponse;
import gt.edu.uinsight.alert.service.RiskEvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/risk-evaluation")
@Tag(name = "B7 - Reglas y evaluación de riesgo",
        description = "Administra reglas activas del motor de riesgo que se usan para evaluar las secciones y generar alertas.")
public class RiskEvaluationController {

    private final RiskEvaluationService riskEvaluationService;

    public RiskEvaluationController(RiskEvaluationService riskEvaluationService) {
        this.riskEvaluationService = riskEvaluationService;
    }

    @PostMapping("/sections/{sectionId}")
    @Operation(summary = "Evaluar el riesgo de una sección",
            description = "Consolida los indicadores de la sección, evalúa las reglas activas, genera las alertas correspondientes "
                    + "y devuelve el nivel y puntaje de riesgo.")
    @ApiResponse(responseCode = "200", description = "Evaluación completada",
            content = @Content(schema = @Schema(implementation = RiskEvaluationResponse.class)))
    @ApiResponse(responseCode = "400", description = "La sección o los datos usados para la evaluación no son válidos")
    @ApiResponse(responseCode = "422", description = "No hay reglas evaluables o datos analíticos suficientes")
    @ApiResponse(responseCode = "500", description = "No fue posible completar la evaluación")
    public ResponseEntity<RiskEvaluationResponse> evaluateSectionRisk(
            @Parameter(description = "Identificador de la sección a evaluar", example = "10")
            @PathVariable Long sectionId) {
        try {
            RiskEvaluationResponse response = riskEvaluationService.evaluateSectionRisk(sectionId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
