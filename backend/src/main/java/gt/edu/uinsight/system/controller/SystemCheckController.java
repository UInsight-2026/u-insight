package gt.edu.uinsight.system.controller;

import gt.edu.uinsight.system.dto.request.CreateCheckRequest;
import gt.edu.uinsight.system.dto.response.CheckResponse;
import gt.edu.uinsight.system.service.SystemCheckService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

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
    @Operation(summary = "Listar todas las comprobaciones registradas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de comprobaciones")
    })
    public ResponseEntity<List<CheckResponse>> getAllChecks() {
        return ResponseEntity.ok(service.getAllChecks());
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
}
