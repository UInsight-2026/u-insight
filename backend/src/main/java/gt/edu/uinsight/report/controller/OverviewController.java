package gt.edu.uinsight.report.controller;

import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.OverviewResponse;
import gt.edu.uinsight.report.exception.ApiError;
import gt.edu.uinsight.report.service.OverviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Celula C5 - Reporte general del sistema.
 * Solo lectura: C5 no crea, actualiza ni elimina informacion.
 */
@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reportes (C5)", description = "Consultas consolidadas para el dashboard de U-Insight")
public class OverviewController {

    private final OverviewService overviewService;

    public OverviewController(OverviewService overviewService) {
        this.overviewService = overviewService;
    }

    @GetMapping("/overview")
    @Operation(summary = "Reporte general del sistema",
            description = "Indicadores globales del dashboard: alertas activas, secciones en "
                    + "alto riesgo, estudiantes en riesgo y tendencia general.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = OverviewResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parametro de filtro invalido",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<OverviewResponse> getOverview(
            @Parameter(description = "Periodo academico, ej. 2026-2") @RequestParam(required = false) String period,
            @Parameter(description = "Codigo de curso, ej. PROG2") @RequestParam(required = false) String course,
            @Parameter(description = "Codigo de docente, ej. DOC-101") @RequestParam(required = false) String teacher,
            @Parameter(description = "Nombre de seccion, ej. A") @RequestParam(required = false) String section,
            @Parameter(description = "Nivel de riesgo: LOW, MEDIUM, HIGH") @RequestParam(required = false) String riskLevel,
            @Parameter(description = "Estado de la alerta") @RequestParam(required = false) String alertStatus) {

        ReportFilter filter = new ReportFilter(period, course, teacher, section, riskLevel, alertStatus);
        return ResponseEntity.ok(overviewService.getOverview(filter));
    }
}
