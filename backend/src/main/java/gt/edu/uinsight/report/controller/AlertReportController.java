package gt.edu.uinsight.report.controller;

import gt.edu.uinsight.common.exception.ApiError;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.AlertReportResponse;
import gt.edu.uinsight.report.service.AlertReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reportes", description = "Consultas consolidadas para el dashboard de U-Insight (Celula C5)")
public class AlertReportController {

    private final OverviewService overviewService;
    private final CourseReportService courseReportService;
    private final SectionReportService sectionReportService;
    private final AlertReportService alertReportService;

    public AlertReportController(OverviewService overviewService,
                             CourseReportService courseReportService,
                             SectionReportService sectionReportService,
                             AlertReportService alertReportService) {
        this.overviewService = overviewService;
        this.courseReportService = courseReportService;
        this.sectionReportService = sectionReportService;
        this.alertReportService = alertReportService;
    }

    @GetMapping("/alerts")
    @Operation(summary = "Listado consolidado de alertas",
            description = "Alertas generadas en la plataforma, con soporte de filtros.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = AlertReportResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parametro de filtro invalido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<AlertReportResponse> getAlerts(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String teacher,
            @RequestParam(required = false) String section,
            @Parameter(description = "Nivel de riesgo: LOW, MEDIUM, HIGH") @RequestParam(required = false) String riskLevel,
            @Parameter(description = "Estado de la alerta, ej. NEW, UNDER_REVIEW, IN_PROGRESS, RESOLVED, DISMISSED")
            @RequestParam(required = false) String alertStatus) {

        ReportFilter filter = new ReportFilter(period, course, teacher, section, riskLevel, alertStatus);
        return ResponseEntity.ok(alertReportService.getAlerts(filter));
    }
}
