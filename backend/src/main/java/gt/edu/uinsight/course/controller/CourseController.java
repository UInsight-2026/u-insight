package gt.edu.uinsight.course.controller;

import gt.edu.uinsight.academicperiod.support.dto.ChangeStatusRequest;
import gt.edu.uinsight.academicperiod.support.dto.PageResponse;
import gt.edu.uinsight.academicperiod.support.exception.AcademicErrorResponse;
import gt.edu.uinsight.course.dto.request.CreateCourseRequest;
import gt.edu.uinsight.course.dto.request.UpdateCourseRequest;
import gt.edu.uinsight.course.dto.response.CourseResponse;
import gt.edu.uinsight.course.service.CourseService;
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
 * Endpoints de cursos (celula A1). Sin logica de negocio: todo se delega a
 * {@link CourseService}. No se expone DELETE (RN-05): la baja es por estado.
 */
@Tag(name = "A1 - Cursos", description = "Gestion de cursos (celula A1)")
@RestController
@RequestMapping("/api/v1/courses")
@ApiResponses({
        @ApiResponse(responseCode = "500", description = "Error interno",
                content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class)))
})
public class CourseController {

    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @Operation(summary = "Crear un curso",
            description = "El curso nace en estado ACTIVE. Aplica RN-01 y RN-08.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Curso creado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o creditos <= 0 (RN-08)",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Codigo repetido, sin distinguir mayusculas (RN-01)",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse create(@Valid @RequestBody CreateCourseRequest request) {
        return service.create(request);
    }

    @Operation(summary = "Listar cursos",
            description = "Paginado (page, size, sort). Filtro opcional ?status=ACTIVE|INACTIVE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagina de cursos"),
            @ApiResponse(responseCode = "400", description = "Estado o parametro de orden no reconocido",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class)))
    })
    @GetMapping
    public PageResponse<CourseResponse> findAll(
            @Parameter(description = "ACTIVE o INACTIVE") @RequestParam(required = false) String status,
            @ParameterObject @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return service.findAll(status, pageable);
    }

    @Operation(summary = "Obtener un curso por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso encontrado"),
            @ApiResponse(responseCode = "400", description = "Id no numerico",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "El curso no existe",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public CourseResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Obtener un curso por codigo",
            description = "La busqueda no distingue mayusculas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un curso con ese codigo",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class)))
    })
    @GetMapping("/code/{code}")
    public CourseResponse findByCode(@PathVariable String code) {
        return service.findByCode(code);
    }

    @Operation(summary = "Actualizar un curso",
            description = "Modifica nombre, descripcion y creditos. El codigo no cambia. Aplica RN-08.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Curso actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o creditos <= 0 (RN-08)",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "El curso no existe",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public CourseResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCourseRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Cambiar el estado de un curso",
            description = "ACTIVE <-> INACTIVE. Es la baja logica del curso (RN-05); "
                    + "un curso INACTIVE no se ofrece para nuevas secciones (RN-09).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado cambiado"),
            @ApiResponse(responseCode = "400", description = "Estado no reconocido",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "El curso no existe",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El curso ya esta en ese estado",
                    content = @Content(schema = @Schema(implementation = AcademicErrorResponse.class)))
    })
    @PatchMapping("/{id}/status")
    public CourseResponse changeStatus(@PathVariable Long id, @Valid @RequestBody ChangeStatusRequest request) {
        return service.changeStatus(id, request);
    }
}
