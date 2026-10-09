package gt.edu.uinsight.academicperiod.controller;

import gt.edu.uinsight.academicperiod.dto.request.CreateAcademicPeriodRequest;
import gt.edu.uinsight.academicperiod.dto.request.UpdateAcademicPeriodRequest;
import gt.edu.uinsight.academicperiod.dto.response.AcademicPeriodResponse;
import gt.edu.uinsight.academicperiod.service.AcademicPeriodService;
import gt.edu.uinsight.academicperiod.support.dto.ChangeStatusRequest;
import gt.edu.uinsight.academicperiod.support.dto.PageResponse;
import gt.edu.uinsight.academicperiod.support.exception.AcademicErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de periodos academicos (celula A1). Sin logica de negocio: todo se
 * delega a {@link AcademicPeriodService}. No se expone DELETE (RN-05).
 */
@Tag(name = "A1 - Periodos academicos", description = "Gestion de periodos academicos (celula A1)")
@RestController
@RequestMapping("/api/v1/academic-periods")
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class)))
})
public class AcademicPeriodController {

    private final AcademicPeriodService service;

    public AcademicPeriodController(AcademicPeriodService service) {
        this.service = service;
    }

    @Operation(summary = "Crear un periodo academico",
            description = "El periodo nace en estado PLANNED. Aplica RN-02 y RN-07.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Periodo creado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o fechas invertidas (RN-02)",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Nombre repetido en el mismo anio (RN-07)",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AcademicPeriodResponse create(@Valid @RequestBody CreateAcademicPeriodRequest request) {
        return service.create(request);
    }

    @Operation(summary = "Listar periodos academicos",
            description = "Paginado (page, size, sort). Filtro opcional ?status=PLANNED|ACTIVE|CLOSED.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagina de periodos"),
            @ApiResponse(responseCode = "400", description = "Estado o parametro de orden no reconocido",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class)))
    })
    @GetMapping
    public PageResponse<AcademicPeriodResponse> findAll(
            @Parameter(description = "PLANNED, ACTIVE o CLOSED") @RequestParam(required = false) String status,
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return service.findAll(status, pageable);
    }

    @Operation(summary = "Obtener el periodo academico activo",
            description = "Devuelve el unico periodo en estado ACTIVE (RN-03).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Periodo activo"),
            @ApiResponse(responseCode = "404", description = "No hay periodo activo",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class)))
    })
    @GetMapping("/active")
    public AcademicPeriodResponse findActive() {
        return service.findActive();
    }

    @Operation(summary = "Obtener un periodo academico por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Periodo encontrado"),
            @ApiResponse(responseCode = "400", description = "Id no numerico",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "El periodo no existe",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public AcademicPeriodResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Actualizar un periodo academico",
            description = "Modifica nombre y fechas. Aplica RN-02, RN-04 y RN-07.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Periodo actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o fechas invertidas (RN-02)",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "El periodo no existe",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Periodo CLOSED (RN-04) o nombre repetido (RN-07)",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public AcademicPeriodResponse update(@PathVariable Long id,
                                         @Valid @RequestBody UpdateAcademicPeriodRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Cambiar el estado de un periodo academico",
            description = "Transiciones validas: PLANNED -> ACTIVE y ACTIVE -> CLOSED (RN-06). "
                    + "Solo puede haber un periodo ACTIVE (RN-03).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado cambiado"),
            @ApiResponse(responseCode = "400", description = "Estado no reconocido",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "El periodo no existe",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Transicion invalida (RN-04, RN-06) o ya hay un ACTIVE (RN-03)",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class)))
    })
    @PatchMapping("/{id}/status")
    public AcademicPeriodResponse changeStatus(@PathVariable Long id,
                                               @Valid @RequestBody ChangeStatusRequest request) {
        return service.changeStatus(id, request);
    }
}
