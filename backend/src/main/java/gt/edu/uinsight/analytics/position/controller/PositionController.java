package gt.edu.uinsight.analytics.position.controller;

import gt.edu.uinsight.analytics.position.dto.response.SectionPositionResponse;
import gt.edu.uinsight.analytics.position.dto.response.StudentPositionResponse;
import gt.edu.uinsight.analytics.position.service.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * Endpoints de la celula B2 - Medidas de posicionamiento.
 *
 * GET /api/v1/analytics/sections/{id}/position
 *   -> Q1, Q2, Q3 de la seccion, y percentiles adicionales via ?percentiles=25,50,75,90
 *
 * GET /api/v1/analytics/students/{id}/position
 *   -> percentil del estudiante respecto a su seccion. Si el estudiante
 *      esta matriculado en mas de una seccion, debe especificarse con
 *      ?sectionId= cual de ellas usar.
 */
@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "B2 - Medidas de posición",
        description = "Cuartiles, percentiles y posición relativa de estudiantes a partir de sus calificaciones.")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping("/sections/{id}/position")
    @Operation(
            summary = "Calcular cuartiles y percentiles de una sección",
            description = "Devuelve Q1, Q2 y Q3 para las calificaciones de la sección. "
                    + "Si se solicita, también calcula los percentiles indicados.")
    @ApiResponse(responseCode = "200", description = "Medidas de posición calculadas",
            content = @Content(schema = @Schema(implementation = SectionPositionResponse.class)))
    @ApiResponse(responseCode = "400", description = "Uno o más percentiles solicitados están fuera del rango 1 a 99")
    @ApiResponse(responseCode = "404", description = "La sección no existe")
    @ApiResponse(responseCode = "422", description = "La sección no tiene calificaciones disponibles")
    public SectionPositionResponse getSectionPosition(
            @Parameter(description = "Identificador de la sección", example = "10")
            @PathVariable Long id,
            @Parameter(description = "Percentiles adicionales a calcular (valores entre 1 y 99)",
                    example = "25,50,75,90")
            @RequestParam(required = false) List<Integer> percentiles) {
        return positionService.getSectionPosition(id, percentiles == null ? Collections.emptyList() : percentiles);
    }

    @GetMapping("/students/{id}/position")
    @Operation(
            summary = "Obtener la posición de un estudiante",
            description = "Calcula el promedio del estudiante y su percentil respecto a una sección. "
                    + "Si está matriculado en varias secciones, se debe indicar sectionId.")
    @ApiResponse(responseCode = "200", description = "Posición del estudiante calculada",
            content = @Content(schema = @Schema(implementation = StudentPositionResponse.class)))
    @ApiResponse(responseCode = "400", description = "La matrícula del estudiante es ambigua; especifique sectionId")
    @ApiResponse(responseCode = "404", description = "El estudiante o la sección solicitada no existe")
    @ApiResponse(responseCode = "422", description = "No hay calificaciones disponibles para calcular la posición")
    public StudentPositionResponse getStudentPosition(
            @Parameter(description = "Identificador del estudiante", example = "25")
            @PathVariable Long id,
            @Parameter(description = "Sección en la que se calcula la posición cuando el estudiante tiene varias matrículas",
                    example = "10")
            @RequestParam(required = false) Long sectionId) {
        return positionService.getStudentPosition(id, sectionId);
    }
}
