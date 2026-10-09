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
            description = "Devuelve el promedio del estudiante, el promedio de su sección y la diferencia entre ambos."
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
                                      "studentCode": "20240589",
                                      "studentAverage": 85.50,
                                      "sectionAverage": 78.20,
                                      "difference": 7.30
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "ID de estudiante inválido")
    @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    public StudentSummaryResponse getSummary(
            @Parameter(description = "ID del estudiante", example = "1")
            @PathVariable Long id) {
        return studentAnalyticsService.getSummary(id);
    }

    @GetMapping("/{id}/comparison")
    @Operation(
            summary = "Obtener comparación del estudiante",
            description = "Devuelve el resumen del estudiante junto con su percentil dentro de la sección."
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
                                      "studentCode": "20240589",
                                      "studentAverage": 85.50,
                                      "percentile": 72
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "ID de estudiante inválido")
    @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    public StudentComparisonResponse getComparison(
            @Parameter(description = "ID del estudiante", example = "1")
            @PathVariable Long id) {
        return studentAnalyticsService.getComparison(id);
    }

    @GetMapping("/{id}/trend")
    @Operation(
            summary = "Obtener tendencia del estudiante",
            description = "Devuelve la tendencia del rendimiento del estudiante (POSITIVE, NEGATIVE, STABLE, INSUFFICIENT_DATA)."
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
                                      "studentCode": "20240589",
                                      "trend": "POSITIVE"
                                    }
                                    """
                    )
            )
    )
    @ApiResponse(responseCode = "400", description = "ID de estudiante inválido")
    @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    public StudentTrendResponse getTrend(
            @Parameter(description = "ID del estudiante", example = "1")
            @PathVariable Long id) {
        return studentAnalyticsService.getTrend(id);
    }

}