// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.intervention.controller;

import gt.edu.uinsight.intervention.dto.request.CreateInterventionRequest;
import gt.edu.uinsight.intervention.dto.response.InterventionResponse;
import gt.edu.uinsight.intervention.service.InterventionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * Endpoints REST del módulo de intervenciones. Delega toda la lógica en
 * {@link InterventionService}; nunca llama al repositorio directamente ni expone
 * la entidad {@code Intervention}.
 */
@Tag(name = "Intervenciones", description = "Registro y consulta de intervenciones institucionales")
@Validated
@RestController
@RequestMapping("/api/v1")
public class InterventionController {

    private final InterventionService interventionService;

    public InterventionController(InterventionService interventionService) {
        this.interventionService = interventionService;
    }

    @Operation(summary = "Registrar una intervención",
            description = "Crea una intervención asociada a una alerta activa (RN-1, RN-2, RN-3).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Intervención creada"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "La alerta indicada no existe"),
            @ApiResponse(responseCode = "409", description = "La alerta no está activa")
    })
    @PostMapping("/alerts/{alertId}/interventions")
    public ResponseEntity<InterventionResponse> create(
            @Parameter(description = "Id de la alerta", example = "10") @PathVariable Long alertId,
            @Valid @RequestBody CreateInterventionRequest request) {

        InterventionResponse response = interventionService.create(alertId, request);
        return ResponseEntity.created(URI.create("/api/v1/interventions/" + response.id()))
                .body(response);
    }

    @Operation(summary = "Listar intervenciones de una alerta",
            description = "Devuelve las intervenciones de la alerta ordenadas por fecha de inicio. "
                    + "Devuelve una lista vacía si la alerta no tiene intervenciones registradas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "404", description = "La alerta indicada no existe")
    })
    @GetMapping("/alerts/{alertId}/interventions")
    public ResponseEntity<List<InterventionResponse>> listByAlert(
            @Parameter(description = "Id de la alerta", example = "10") @PathVariable Long alertId) {

        return ResponseEntity.ok(interventionService.findByAlert(alertId));
    }

    @Operation(summary = "Obtener una intervención por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Intervención encontrada"),
            @ApiResponse(responseCode = "404", description = "La intervención indicada no existe")
    })
    @GetMapping("/interventions/{id}")
    public ResponseEntity<InterventionResponse> getById(
            @Parameter(description = "Id de la intervención", example = "1") @PathVariable Long id) {

        return ResponseEntity.ok(interventionService.findById(id));
    }
}
