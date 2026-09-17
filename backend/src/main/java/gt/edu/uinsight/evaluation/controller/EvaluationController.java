package gt.edu.uinsight.evaluation.controller;

import gt.edu.uinsight.evaluation.dto.request.CreateEvaluationRequest;
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
            @ApiResponse(responseCode = "400", description = "Error de validación de campos"),
            @ApiResponse(responseCode = "422", description = "Sección no activa o límite de ponderación excedido")
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
        return evaluationService.getAllEvaluations();
    }

    @Operation(summary = "Consultar evaluación por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluación encontrada",
                    content = @Content(schema = @Schema(implementation = EvaluationResponse.class))),
            @ApiResponse(responseCode = "404", description = "No existe una evaluación con ese id")
    })
    @GetMapping("/evaluations/{id}")
    public EvaluationResponse getEvaluationById(
            @Parameter(description = "Identificador de la evaluación", example = "1") @PathVariable Long id) {
        log.info("Petición recibida para consultar evaluación con ID: {}", id);
        return evaluationService.getEvaluationById(id);
    }
} 