package gt.edu.uinsight.system.controller;

import gt.edu.uinsight.system.dto.request.CreateCheckRequest;
import gt.edu.uinsight.system.dto.request.UpdateCheckStatusRequest;
import gt.edu.uinsight.system.dto.response.CheckResponse;
import gt.edu.uinsight.system.entity.CheckStatus;
import gt.edu.uinsight.system.service.SystemCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/system/checks")
@Tag(name = "System - Checks", description = "Registro de comprobaciones tecnicas del sistema")
public class SystemCheckController {

    private final SystemCheckService service;

    public SystemCheckController(SystemCheckService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Registrar una comprobacion tecnica")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Comprobacion registrada"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "500", description = "Error interno")
    })
    public ResponseEntity<CheckResponse> createCheck(
            @Valid @RequestBody CreateCheckRequest request,
            UriComponentsBuilder uriBuilder) {

        CheckResponse created = service.createCheck(request);

        return ResponseEntity
                .created(uriBuilder.path("/api/v1/system/checks/{id}")
                        .buildAndExpand(created.id())
                        .toUri())
                .body(created);
    }

    @GetMapping
    @Operation(summary = "Listar comprobaciones con filtros opcionales y paginacion")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagina de comprobaciones"),
            @ApiResponse(responseCode = "400", description = "Valor invalido en un parametro de filtro")
    })
    public ResponseEntity<Page<CheckResponse>> getChecks(
            @Parameter(description = "Filtro parcial por componente, por ejemplo database")
            @RequestParam(required = false) String component,

            @Parameter(description = "Filtro exacto por estado: UP, DOWN, DEGRADED o UNKNOWN")
            @RequestParam(required = false) CheckStatus status,

            @Parameter(description = "Numero de pagina, empezando en 0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Cantidad de elementos por pagina")
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "checkedAt"));

        return ResponseEntity.ok(service.getChecks(component, status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar una comprobacion por su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comprobacion encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una comprobacion con ese identificador")
    })
    public CheckResponse getCheckById(@PathVariable Long id) {
        return service.getCheckById(id);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cambiar el estado de una comprobacion")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "No existe una comprobacion con ese identificador"),
            @ApiResponse(responseCode = "409", description = "Transicion no permitida: DOWN no pasa directo a UP")
    })
    public ResponseEntity<CheckResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCheckStatusRequest request) {

        return ResponseEntity.ok(service.updateStatus(id, request));
    }
}
