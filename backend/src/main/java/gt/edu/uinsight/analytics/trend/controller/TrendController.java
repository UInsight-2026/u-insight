package gt.edu.uinsight.analytics.trend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.exception.TrendErrorResponse;
import gt.edu.uinsight.analytics.trend.service.TrendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;

@RestController 
@RequestMapping("/api/v1/analytics")
@Validated
@Tag(name = "B4 - Evolución",
        description = "Evolución de las calificaciones de secciones y estudiantes a través de sus evaluaciones.")
public class TrendController {
    
    private final TrendService trendService;

    public TrendController(TrendService trendService) {
        this.trendService = trendService;
    }

     @GetMapping("/sections/{id}/trends")
     @Operation(summary = "Obtener la evolución de una sección",
        description = "Devuelve la clasificación de la tendencia, el cambio promedio y los valores por evaluación.")
     @ApiResponse (responseCode = "200", description = "Tendencia de la sección obtenida exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrendResponse.class)))
     @ApiResponse (responseCode = "404", description = "La sección con ese id no existe",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrendErrorResponse.class)))
    @ApiResponse (responseCode = "400", description = "El identificador debe ser positivo o tiene un formato inválido",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrendErrorResponse.class)))
    @ApiResponse (responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrendErrorResponse.class)))
    
     public ResponseEntity<TrendResponse> getTrendBySectionId(
             @Parameter(description = "Identificador de la sección", example = "10")
             @PathVariable @Positive Long id) {
         // Lógica para obtener la tendencia por ID de sección
         TrendResponse trendResponse = trendService.getTrendBySectionId(id);
         return ResponseEntity.ok(trendResponse);
     }

    @GetMapping("/students/{id}/trends")
    @Operation(summary = "Obtener la evolución de un estudiante",
        description = "Devuelve la clasificación de la tendencia, el cambio promedio y los valores del estudiante por evaluación.")
     @ApiResponse (responseCode = "200", description = "Tendencia del estudiante obtenida exitosamente",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrendResponse.class)))
     @ApiResponse (responseCode = "404", description = "El estudiante con ese id no existe",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrendErrorResponse.class)))
    @ApiResponse (responseCode = "400", description = "El identificador debe ser positivo o tiene un formato inválido",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrendErrorResponse.class)))
    @ApiResponse (responseCode = "500", description = "Error interno del servidor",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = TrendErrorResponse.class)))
    public ResponseEntity<TrendResponse> getTrendByStudentId(
            @Parameter(description = "Identificador del estudiante", example = "25")
            @PathVariable @Positive Long id) {
        // Lógica para obtener la tendencia por ID de estudiante
        TrendResponse trendResponse = trendService.getTrendByStudentId(id);
        return ResponseEntity.ok(trendResponse);
    }

}