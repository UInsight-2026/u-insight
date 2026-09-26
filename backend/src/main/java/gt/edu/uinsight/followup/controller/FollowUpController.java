// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.followup.controller;

import gt.edu.uinsight.followup.dto.request.CreateFollowUpRequest;
import gt.edu.uinsight.followup.dto.response.FollowUpResponse;
import gt.edu.uinsight.followup.service.FollowUpService;
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
 * Endpoints REST del módulo de seguimiento. Delega toda la lógica en
 * {@link FollowUpService}; nunca llama al repositorio directamente ni expone
 * la entidad {@code FollowUp}.
 */
@Tag(name = "Seguimientos", description = "Registro y consulta de seguimientos sobre intervenciones")
@Validated
@RestController
@RequestMapping("/api/v1")
public class FollowUpController {

    private final FollowUpService followUpService;

    public FollowUpController(FollowUpService followUpService) {
        this.followUpService = followUpService;
    }

    @Operation(summary = "Registrar un seguimiento",
            description = "Crea un seguimiento sobre una intervención existente (RN-5, RN-6).")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Seguimiento creado"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "La intervención indicada no existe"),
            @ApiResponse(responseCode = "409", description = "La intervención no está activa, "
                    + "o la fecha del seguimiento es anterior al inicio de la intervención")
    })
    @PostMapping("/interventions/{interventionId}/follow-ups")
    public ResponseEntity<FollowUpResponse> create(
            @Parameter(description = "Id de la intervención", example = "5") @PathVariable Long interventionId,
            @Valid @RequestBody CreateFollowUpRequest request) {

        FollowUpResponse response = followUpService.create(interventionId, request);
        return ResponseEntity.created(
                        URI.create("/api/v1/interventions/" + interventionId + "/follow-ups/" + response.id()))
                .body(response);
    }

    @Operation(summary = "Listar seguimientos de una intervención",
            description = "Devuelve los seguimientos de la intervención ordenados por fecha. "
                    + "Devuelve una lista vacía si la intervención no tiene seguimientos registrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "404", description = "La intervención indicada no existe")
    })
    @GetMapping("/interventions/{interventionId}/follow-ups")
    public ResponseEntity<List<FollowUpResponse>> listByIntervention(
            @Parameter(description = "Id de la intervención", example = "5") @PathVariable Long interventionId) {

        return ResponseEntity.ok(followUpService.listByIntervention(interventionId));
    }
}
