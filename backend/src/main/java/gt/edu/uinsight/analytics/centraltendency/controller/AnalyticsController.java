package gt.edu.uinsight.analytics.centraltendency.controller;

import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.centraltendency.service.CentralTendencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Tendencia Central B1", description = "Endpoints para el calculo de medidas de tendencia central")
public class AnalyticsController {

    private final CentralTendencyService centralTendencyService;

    public AnalyticsController(CentralTendencyService centralTendencyService) {
        this.centralTendencyService = centralTendencyService;
    }

    @GetMapping("/sections/{id}/central-tendency")
    @Operation(
            summary = "Obtener tendencia central de una sección",
            description = "Delega el calculo de la media, mediana y moda de las calificaciones de una seccion al servicio. " +
                    "Si se envia evaluationId, el calculo se limita a esa evaluacion."
    )
    @ApiResponse(responseCode = "200", description = "Calculo procesado exitosamente (sampleSize=0 si la seccion aun no tiene calificaciones; no se considera error)")
    @ApiResponse(responseCode = "404", description = "La seccion con ese id no existe")
    @ApiResponse(responseCode = "400", description = "El id no es un numero valido, o evaluationId no pertenece a la seccion")
    public ResponseEntity<CentralTendencyResponse> getSectionCentralTendency(
            @Parameter(description = "ID de la seccion", example = "10") @PathVariable("id") Long id,
            @Parameter(description = "Filtra el calculo a una sola evaluacion en vez de toda la seccion", example = "45")
            @RequestParam(value = "evaluationId", required = false) Long evaluationId) {

        CentralTendencyResponse response = centralTendencyService.getSectionCentralTendency(id, evaluationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/{id}/central-tendency")
    @Operation(
            summary = "Obtener tendencia central de un curso",
            description = "Delega el calculo de la media, mediana y moda de las calificaciones de un curso al servicio. " +
                    "Si se envia periodId, limita el calculo a ese periodo academico."
    )
    @ApiResponse(responseCode = "200", description = "Calculo procesado exitosamente (sampleSize=0 si ninguna seccion tiene calificaciones; no se considera error)")
    @ApiResponse(responseCode = "404", description = "El curso con ese id no existe")
    @ApiResponse(responseCode = "400", description = "El id no es un numero valido, o periodId no corresponde a un periodo academico valido")
    public ResponseEntity<CentralTendencyResponse> getCourseCentralTendency(
            @Parameter(description = "ID del curso", example = "5") @PathVariable("id") Long id,
            @Parameter(description = "Limita el calculo a un periodo academico especifico", example = "2")
            @RequestParam(value = "periodId", required = false) Long periodId) {

        CentralTendencyResponse response = centralTendencyService.getCourseCentralTendency(id, periodId);
        return ResponseEntity.ok(response);
    }
}