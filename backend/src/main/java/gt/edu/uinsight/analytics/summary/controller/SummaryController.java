package gt.edu.uinsight.analytics.summary.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.service.SummaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/analytics/sections")
@Tag(
    name = "B6 - Consolidación analítica",
    description = "Combina los resultados de B1 a B5 en un solo resumen por sección"
)
public class SummaryController {

    private final SummaryService summaryService;

    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @Operation(
        summary = "Obtener resumen de consolidado de una sección.",
        description = "Devuelve tendencia central, posición, dispersión y evolución de la sección indicada, "
                    + "más el conteo de estudiantes en riesgo. Es una entrega parcial de semana 2: aún no incluye "
                    + "manejo de errores 404/500 (planificado para semana 3) ni el DTO Response definitivo (Mapper pendiente)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resumen generado correctamente")
    })
    @GetMapping("/{sectionId}/summary")
    public SectionSummary getSummary(@PathVariable Long sectionId) {
        return summaryService.getSummary(sectionId);
    }
}
