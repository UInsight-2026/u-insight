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
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
 
@Tag(name = "Evaluations", description = "Gestión del ciclo de vida de las evaluaciones académicas (célula A5)")
@RestController
@RequestMapping("/api/v1")
public class EvaluationController {
 
    private static final Logger log = LoggerFactory.getLogger(EvaluationController.class);
 
    private final EvaluationService evaluationService;
 
    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }
 
    @Operation(summary = "Crear evaluación",
            description = "Registra una nueva evaluación en estado DRAFT para una sección activa (HU1).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evaluación creada",
                    content = @Content(schema = @Schema(implementation = EvaluationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación de campos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Sección no activa o límite de ponderación excedido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/evaluations")
    public ResponseEntity<EvaluationResponse> createEvaluation(@Valid @RequestBody CreateEvaluationRequest request) {
        log.info("Petición recibida para crear evaluación en la sección ID: {}", request.getSectionId());
        EvaluationResponse response = evaluationService.createEvaluation(request);
        log.info("RESOURCE_CREATED: Evaluación creada con éxito con ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
 
    @Operation(summary = "Listar todas las evaluaciones")
    @ApiResponse(responseCode = "200", description = "Listado de evaluaciones")
    @GetMapping("/evaluations")
    public List<EvaluationResponse> getAllEvaluations() {
        log.info("Petición recibida para listar todas las evaluaciones");
        List<EvaluationResponse> response = evaluationService.getAllEvaluations();
        log.info("RESOURCE_LISTED: {} evaluaciones devueltas", response.size());
        return response;
    }
 
    @Operation(summary = "Consultar evaluación por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluación encontrada",
                    content = @Content(schema = @Schema(implementation = EvaluationResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe una evaluación con ese id",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/evaluations/{id}")
    public EvaluationResponse getEvaluationById(
            @Parameter(description = "Identificador de la evaluación", example = "1") @PathVariable Long id) {
        log.info("Petición recibida para consultar evaluación con ID: {}", id);
        EvaluationResponse response = evaluationService.getEvaluationById(id);
        log.info("RESOURCE_FOUND: evaluación ID {} encontrada", id);
        return response;
    }
 
    @Operation(summary = "Listar evaluaciones de una sección (HU2)",
            description = "Consumido principalmente por la célula A6 para validar el registro de calificaciones.")
    @ApiResponse(responseCode = "200", description = "Listado de evaluaciones de la sección (puede ser vacío)")
    @GetMapping("/sections/{id}/evaluations")
    public List<EvaluationResponse> getEvaluationsBySection(
            @Parameter(description = "Identificador de la sección", example = "10") @PathVariable Long id) {
        log.info("Petición recibida para listar evaluaciones de la sección ID: {}", id);
        List<EvaluationResponse> response = evaluationService.getEvaluationsBySectionId(id);
        log.info("RESOURCE_LISTED: {} evaluaciones devueltas para la sección ID: {}", response.size(), id);
        return response;
    }
 
    @Operation(summary = "Actualizar evaluación (HU3)",
            description = "Permite corregir nombre, fecha, nota máxima o ponderación mientras la evaluación no esté CLOSED (RN5).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluación actualizada",
                    content = @Content(schema = @Schema(implementation = EvaluationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación de campos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe una evaluación con ese id",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "La evaluación está CLOSED y no puede modificarse",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Límite de ponderación excedido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/evaluations/{id}")
    public EvaluationResponse updateEvaluation(
            @Parameter(description = "Identificador de la evaluación", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateEvaluationRequest request) {
        log.info("Petición recibida para actualizar evaluación con ID: {}", id);
        EvaluationResponse response = evaluationService.updateEvaluation(id, request);
        log.info("RESOURCE_UPDATED: evaluación ID {} actualizada con éxito", id);
        return response;
    }
 
    @Operation(summary = "Cambiar estado de la evaluación (HU4)",
            description = "Controla el ciclo de vida DRAFT -> ACTIVE -> CLOSED, o DRAFT/ACTIVE -> CANCELLED (RN6).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado",
                    content = @Content(schema = @Schema(implementation = EvaluationResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe una evaluación con ese id",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Transición de estado no permitida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/evaluations/{id}/status")
    public EvaluationResponse changeStatus(
            @Parameter(description = "Identificador de la evaluación", example = "1") @PathVariable Long id,
            @Valid @RequestBody ChangeEvaluationStatusRequest request) {
        log.info("Petición recibida para cambiar estado de evaluación ID: {} a {}", id, request.getStatus());
        EvaluationResponse response = evaluationService.changeStatus(id, request);
        log.info("RESOURCE_STATUS_CHANGED: evaluación ID {} cambió a estado {}", id, response.getStatus());
        return response;
    }
}