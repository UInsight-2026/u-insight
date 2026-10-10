package gt.edu.uinsight.analytics.summary.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.service.SectionValidationService;
import gt.edu.uinsight.analytics.summary.service.SummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/analytics/sections")
@Tag(
    name = "B6 - Consolidación analítica",
    description = "Combina resultados analíticos de sección de B1 a B4"
)
public class SummaryController {

    private final SummaryService summaryService;
    private final SectionValidationService sectionValidationService;

    public SummaryController(
            SummaryService summaryService,
            SectionValidationService sectionValidationService) {
        this.summaryService = summaryService;
        this.sectionValidationService = sectionValidationService;
    }

    @Operation(
        summary = "Obtener resumen de consolidado de una sección.",
        description = "Devuelve tendencia central, posición, dispersión y evolución de la sección indicada, "
                    + "más el conteo de estudiantes en riesgo."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resumen generado correctamente"),
        @ApiResponse(responseCode = "206", description = "Resumen generado parcialmente; uno o más componentes no están disponibles"),
        @ApiResponse(responseCode = "404", description = "Sección no encontrada"),
        @ApiResponse(responseCode = "500", description = "Error inesperado en el servidor")
    })
    @GetMapping("/{sectionId}/summary")
    public ResponseEntity<SectionSummary> getSummary(@PathVariable Long sectionId) {

        if (!sectionValidationService.exists(sectionId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        SectionSummary summary = summaryService.getSummary(sectionId);

        if (summary.getUnavailableComponents() != null
                && !summary.getUnavailableComponents().isEmpty()) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(summary);
        }

        return ResponseEntity.ok(summary);
    }
}