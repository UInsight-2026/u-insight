package gt.edu.uinsight.evaluation.controller;

import gt.edu.uinsight.evaluation.dto.request.ChangeStatusRequest;
import gt.edu.uinsight.evaluation.dto.request.CreateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.request.UpdateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.response.EvaluationResponse;
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
@RequestMapping("/api/v1/evaluations")
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
            @ApiResponse(responseCode = "400", description = "Error de validación de campos"),
            @ApiResponse(responseCode = "422", description = "Sección no activa o límite de ponderación excedido")
    })
    @PostMapping()
    public ResponseEntity<EvaluationResponse> createEvaluation(@Valid @RequestBody CreateEvaluationRequest request) {
        log.info("Petición recibida para crear evaluación en la sección ID: {}", request.getSectionId());
        EvaluationResponse response = evaluationService.createEvaluation(request);
        log.info("RESOURCE_CREATED: Evaluación creada con éxito con ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar todas las evaluaciones")
    @ApiResponse(responseCode = "200", description = "Listado de evaluaciones")
    @GetMapping()
    public List<EvaluationResponse> getAllEvaluations() {
        log.info("Petición recibida para listar todas las evaluaciones");
        return evaluationService.getAllEvaluations();
    }

    @Operation(summary = "Consultar evaluación por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluación encontrada",
                    content = @Content(schema = @Schema(implementation = EvaluationResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe una evaluación con ese id")
    })
    @GetMapping("/{id}")
    public EvaluationResponse getEvaluationById(
            @Parameter(description = "Identificador de la evaluación", example = "1") @PathVariable Long id) {
        log.info("Petición recibida para consultar evaluación con ID: {}", id);
        return evaluationService.getEvaluationById(id);
    }

    @Operation(summary = "Actualizar evaluación",
            description = "Actualiza nombre, fecha, nota máxima y ponderación (HU3). No permite modificar evaluaciones en estado CLOSED (RN5).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluación actualizada",
                    content = @Content(schema = @Schema(implementation = EvaluationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error de validación de campos"),
            @ApiResponse(responseCode = "404", description = "No existe una evaluación con ese id"),
            @ApiResponse(responseCode = "409", description = "La evaluación está en estado CLOSED y no puede modificarse")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EvaluationResponse> updateEvaluation(
            @Parameter(description = "Identificador de la evaluación", example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateEvaluationRequest request) {
        log.info("Petición recibida para actualizar evaluación con ID: {}", id);
        EvaluationResponse response = evaluationService.updateEvaluation(id, request);
        log.info("RESOURCE_UPDATED: Evaluación actualizada con ID: {}", id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cambiar estado de la evaluación",
            description = "Aplica una transición de estado válida (HU4). Solo permite DRAFT->ACTIVE, ACTIVE->CLOSED, o DRAFT/ACTIVE->CANCELLED (RN6).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado",
                    content = @Content(schema = @Schema(implementation = EvaluationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "404", description = "No existe una evaluación con ese id"),
            @ApiResponse(responseCode = "409", description = "Transición de estado no permitida")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<EvaluationResponse> changeStatus(
            @Parameter(description = "Identificador de la evaluación", example = "1") @PathVariable Long id,
            @Valid @RequestBody ChangeStatusRequest request) {
        log.info("Petición recibida para cambiar estado de evaluación ID: {} a {}", id, request.getStatus());
        EvaluationResponse response = evaluationService.changeStatus(id, request.getStatus());
        log.info("RESOURCE_UPDATED: Estado de evaluación ID: {} cambiado a {}", id, request.getStatus());
        return ResponseEntity.ok(response);
    }
}
