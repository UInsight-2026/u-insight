package gt.edu.uinsight.analytics.individual.controller;

import gt.edu.uinsight.analytics.individual.dto.response.StudentComparisonResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentSummaryResponse;
import gt.edu.uinsight.analytics.individual.dto.response.StudentTrendResponse;
import gt.edu.uinsight.analytics.individual.service.StudentAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics/students")
@Tag(name = "Análisis Individual B5", description = "Endpoints para el análisis del rendimiento individual del estudiante")
public class StudentAnalyticsController {

    private final StudentAnalyticsService studentAnalyticsService;

    public StudentAnalyticsController(StudentAnalyticsService studentAnalyticsService) {
        this.studentAnalyticsService = studentAnalyticsService;
    }

    @GetMapping("/{id}/summary")
    @Operation(
            summary = "Obtener resumen del estudiante",
            description = "Devuelve promedios y diferencia usando la base de datos. Si el estudiante tiene varias inscripciones vigentes, debe enviarse sectionId. Se aceptan secciones cerradas si la inscripción sigue vigente."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Resumen individual obtenido exitosamente",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StudentSummaryResponse.class),
                    examples = @ExampleObject(
                            name = "Ejemplo de resumen",
                            value = """
                                    {
                                      "studentCode": "EST2024001",
                                      "studentAverage": 85.5,
                                      "sectionAverage": 78.2,
                                      "difference": 7.3
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "ID inválido, el estudiante no está inscrito en sectionId o sectionId requerido por tener varias inscripciones")
    @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    @ApiResponse(responseCode = "422", description = "El estudiante o la sección no tiene datos analíticos disponibles")
    public StudentSummaryResponse getSummary(
            @Parameter(description = "ID del estudiante", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Sección en la que el estudiante tiene inscripción vigente. Obligatoria si tiene varias inscripciones.", example = "2")
            @RequestParam(required = false) Long sectionId) {
        return studentAnalyticsService.getSummary(id, sectionId);
    }

    @GetMapping("/{id}/comparison")
    @Operation(
            summary = "Obtener comparación del estudiante",
            description = "Devuelve promedios y percentil usando la base de datos. Si el estudiante tiene varias inscripciones vigentes, debe enviarse sectionId. Se aceptan secciones cerradas si la inscripción sigue vigente."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Comparación obtenida exitosamente",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StudentComparisonResponse.class),
                    examples = @ExampleObject(
                            name = "Ejemplo de comparación",
                            value = """
                                    {
                                      "studentCode": "EST2024001",
                                      "studentAverage": 85.5,
                                      "sectionAverage": 78.2,
                                      "difference": 7.3,
                                      "percentile": 72
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "ID inválido, el estudiante no está inscrito en sectionId o sectionId requerido por tener varias inscripciones")
    @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    @ApiResponse(responseCode = "422", description = "El estudiante o la sección no tiene datos analíticos disponibles")
    public StudentComparisonResponse getComparison(
            @Parameter(description = "ID del estudiante", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Sección en la que el estudiante tiene inscripción vigente. Obligatoria si tiene varias inscripciones.", example = "2")
            @RequestParam(required = false) Long sectionId) {
        return studentAnalyticsService.getComparison(id, sectionId);
    }

    @GetMapping("/{id}/trend")
    @Operation(
            summary = "Obtener tendencia del estudiante",
            description = "Devuelve la tendencia del rendimiento del estudiante (POSITIVE, NEGATIVE, STABLE, INSUFFICIENT_DATA) y el cambio promedio entre evaluaciones."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Tendencia obtenida exitosamente",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StudentTrendResponse.class),
                    examples = @ExampleObject(
                            name = "Ejemplo de tendencia",
                            value = """
                                    {
                                      "studentCode": "EST2024001",
                                      "trend": "POSITIVE",
                                      "averageChange": 3.5
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "ID de estudiante con formato inválido")
    @ApiResponse(responseCode = "404", description = "ID de estudiante inválido")
    public StudentTrendResponse getTrend(
            @Parameter(description = "ID del estudiante", example = "1")
            @PathVariable Long id) {
        return studentAnalyticsService.getTrend(id);
    }

}
