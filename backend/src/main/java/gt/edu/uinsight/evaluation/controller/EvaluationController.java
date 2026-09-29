// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.controller;

import gt.edu.uinsight.evaluation.dto.request.ChangeEvaluationStatusRequest;
import gt.edu.uinsight.evaluation.dto.request.CreateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.request.UpdateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.response.EvaluationResponse;
import gt.edu.uinsight.evaluation.exception.ErrorResponse;
import gt.edu.uinsight.evaluation.service.EvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API REST del módulo de evaluaciones (célula A5).
 * Contrato: sección 6 del documento de la célula A5 y sección 7.1 del documento base.
 */
@Tag(name = "Evaluations", description = "Gestión del ciclo de vida de las evaluaciones académicas (célula A5)")
@RestController
@RequestMapping("/api/v1")
public class EvaluationController {

    private static final Logger log = LoggerFactory.getLogger(EvaluationController.class);

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @Operation(summary = "Crear evaluación (HU1)",
            description = "Registra una evaluación en estado DRAFT. La sección debe existir y estar ACTIVE (RN1) "
                    + "y la suma de ponderaciones de la sección no puede superar 100% (RN4).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evaluación creada",
                    content = @Content(schema = @Schema(implementation = EvaluationResponse.class))),
            @ApiResponse(responseCode = "400", description = "VALIDATION_ERROR / MALFORMED_REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "SECTION_NOT_FOUND",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "SECTION_NOT_ACTIVE / WEIGHT_LIMIT_EXCEEDED",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/evaluations")
    public ResponseEntity<EvaluationResponse> createEvaluation(@Valid @RequestBody CreateEvaluationRequest request) {
        log.info("EVALUATION_REQUEST operation=CREATE method=POST path=/api/v1/evaluations sectionId={}",
                request.getSectionId());
        EvaluationResponse response = evaluationService.createEvaluation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar todas las evaluaciones")
    @ApiResponse(responseCode = "200", description = "Listado de evaluaciones",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = EvaluationResponse.class))))
    @GetMapping("/evaluations")
    public List<EvaluationResponse> getAllEvaluations() {
        log.info("EVALUATION_REQUEST operation=LIST method=GET path=/api/v1/evaluations");
        return evaluationService.getAllEvaluations();
    }

    @Operation(summary = "Consultar evaluación por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluación encontrada",
                    content = @Content(schema = @Schema(implementation = EvaluationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Id con formato inválido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "EVALUATION_NOT_FOUND",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/evaluations/{id}")
    public EvaluationResponse getEvaluationById(
            @Parameter(description = "Identificador de la evaluación", example = "1") @PathVariable Long id) {
        log.info("EVALUATION_REQUEST operation=GET method=GET path=/api/v1/evaluations/{} evaluationId={}", id, id);
        return evaluationService.getEvaluationById(id);
    }

    @Operation(summary = "Listar evaluaciones de una sección (HU2)",
            description = "Devuelve las evaluaciones de la sección en cualquier estado. Si la sección existe y no "
                    + "tiene evaluaciones responde 200 con lista vacía. Consumido por A6 y la Sección B.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado (puede ser vacío)",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EvaluationResponse.class)))),
            @ApiResponse(responseCode = "404", description = "SECTION_NOT_FOUND",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/sections/{id}/evaluations")
    public List<EvaluationResponse> getEvaluationsBySection(
            @Parameter(description = "Identificador de la sección", example = "1") @PathVariable Long id) {
        log.info("EVALUATION_REQUEST operation=LIST_BY_SECTION method=GET path=/api/v1/sections/{}/evaluations sectionId={}",
                id, id);
        return evaluationService.getEvaluationsBySectionId(id);
    }

    @Operation(summary = "Actualizar evaluación (HU3)",
            description = "Corrige nombre, fecha, nota máxima o ponderación. Solo se permite en DRAFT o ACTIVE "
                    + "(RN5: CLOSED y CANCELLED no se modifican). Si está ACTIVE, se sincroniza con A6.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluación actualizada",
                    content = @Content(schema = @Schema(implementation = EvaluationResponse.class))),
            @ApiResponse(responseCode = "400", description = "VALIDATION_ERROR / MALFORMED_REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "EVALUATION_NOT_FOUND",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "EVALUATION_NOT_EDITABLE (CLOSED o CANCELLED)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "SECTION_NOT_ACTIVE / WEIGHT_LIMIT_EXCEEDED",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/evaluations/{id}")
    public EvaluationResponse updateEvaluation(
            @Parameter(description = "Identificador de la evaluación", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateEvaluationRequest request) {
        log.info("EVALUATION_REQUEST operation=UPDATE method=PUT path=/api/v1/evaluations/{} evaluationId={}", id, id);
        return evaluationService.updateEvaluation(id, request);
    }

    @Operation(summary = "Cambiar estado de la evaluación (HU4)",
            description = "Ciclo de vida (RN6): DRAFT -> ACTIVE -> CLOSED, o DRAFT/ACTIVE -> CANCELLED. "
                    + "Al activar se valida que la sección siga ACTIVE y la evaluación se publica a A6.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado",
                    content = @Content(schema = @Schema(implementation = EvaluationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Estado inexistente (VALIDATION_ERROR)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "EVALUATION_NOT_FOUND",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "INVALID_STATUS_TRANSITION",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "SECTION_NOT_ACTIVE (al activar)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/evaluations/{id}/status")
    public EvaluationResponse changeStatus(
            @Parameter(description = "Identificador de la evaluación", example = "1") @PathVariable Long id,
            @Valid @RequestBody ChangeEvaluationStatusRequest request) {
        log.info("EVALUATION_REQUEST operation=CHANGE_STATUS method=PATCH path=/api/v1/evaluations/{}/status evaluationId={} to={}",
                id, id, request.getStatus());
        return evaluationService.changeStatus(id, request);
    }
}
