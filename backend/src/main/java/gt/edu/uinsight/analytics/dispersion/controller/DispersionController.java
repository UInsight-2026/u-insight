package gt.edu.uinsight.analytics.dispersion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.dispersion.service.DispersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "B3 - Dispersión",
        description = "Rango, varianza, desviación estándar y clasificación de la dispersión de calificaciones.")
public class DispersionController {

    private final DispersionService dispersionService;

    public DispersionController(
            DispersionService dispersionService) {
        this.dispersionService = dispersionService;
    }

    @GetMapping("/sections/{id}/dispersion")
    @Operation(summary = "Calcular la dispersión de una sección",
            description = "Calcula mínimo, máximo, rango, varianza, desviación estándar y clasificación "
                    + "de las calificaciones de una sección.")
    @ApiResponse(responseCode = "200", description = "Métricas de dispersión calculadas",
            content = @Content(schema = @Schema(implementation = DispersionResponse.class)))
    @ApiResponse(responseCode = "404", description = "La sección no existe")
    @ApiResponse(responseCode = "422", description = "El identificador o los datos de calificaciones no son válidos o son insuficientes")
    public ResponseEntity<DispersionResponse> getSectionDispersion(
            @Parameter(description = "Identificador de la sección", example = "10")
            @PathVariable("id") Long sectionId) {

        return ResponseEntity.ok(
                dispersionService.getSectionDispersion(sectionId));
    }

    @GetMapping("/courses/{id}/dispersion")
    @Operation(summary = "Calcular la dispersión de un curso",
            description = "Calcula las métricas de dispersión de las calificaciones agregadas de las secciones del curso.")
    @ApiResponse(responseCode = "200", description = "Métricas de dispersión calculadas",
            content = @Content(schema = @Schema(implementation = DispersionResponse.class)))
    @ApiResponse(responseCode = "404", description = "El curso no existe o no tiene secciones")
    @ApiResponse(responseCode = "422", description = "El identificador o los datos de calificaciones no son válidos o son insuficientes")
    public ResponseEntity<DispersionResponse> getCourseDispersion(
            @Parameter(description = "Identificador del curso", example = "5")
            @PathVariable("id") Long courseId) {

        return ResponseEntity.ok(
                dispersionService.getCourseDispersion(courseId));
    }
}
