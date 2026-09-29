package gt.edu.uinsight.evaluation.controller;

import gt.edu.uinsight.evaluation.dto.response.EvaluationResponse;
import gt.edu.uinsight.evaluation.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Evaluations", description = "Consulta de evaluaciones por sección (célula A5)")
@RestController
@RequestMapping("/api/v1/sections")
public class SectionEvaluationController {

    private static final Logger log = LoggerFactory.getLogger(SectionEvaluationController.class);

    private final EvaluationService evaluationService;

    public SectionEvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @Operation(summary = "Listar evaluaciones de una sección",
            description = "Devuelve las evaluaciones asociadas a una sección (HU2). Si la sección no tiene evaluaciones, responde 200 con una lista vacía.")
    @ApiResponse(responseCode = "200", description = "Listado de evaluaciones de la sección")
    @GetMapping("/{id}/evaluations")
    public List<EvaluationResponse> getEvaluationsBySection(
            @Parameter(description = "Identificador de la sección", example = "1") @PathVariable Long id) {
        log.info("Petición recibida para listar evaluaciones de la sección ID: {}", id);
        return evaluationService.getEvaluationsBySectionId(id);
    }
}
