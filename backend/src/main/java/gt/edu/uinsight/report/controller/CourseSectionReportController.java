package gt.edu.uinsight.report.controller;

import gt.edu.uinsight.common.exception.ApiError;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.AlertReportResponse;
import gt.edu.uinsight.report.dto.response.CourseReportResponse;
import gt.edu.uinsight.report.dto.response.OverviewResponse;
import gt.edu.uinsight.report.dto.response.SectionReportResponse;
import gt.edu.uinsight.report.service.AlertReportService;
import gt.edu.uinsight.report.service.CourseReportService;
import gt.edu.uinsight.report.service.OverviewService;
import gt.edu.uinsight.report.service.SectionReportService;
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

/**
 * Celula C5 - Reportes y consultas consolidadas.
 * Todos los endpoints son GET (solo lectura): C5 no crea, actualiza ni
 * elimina informacion.
 */
@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reportes", description = "Consultas consolidadas para el dashboard de U-Insight (Celula C5)")
public class CourseSectionReportController {

    private final OverviewService overviewService;
    private final CourseReportService courseReportService;
    private final SectionReportService sectionReportService;
    private final AlertReportService alertReportService;

    public CourseSectionReportController(OverviewService overviewService,
                             CourseReportService courseReportService,
                             SectionReportService sectionReportService,
                             AlertReportService alertReportService) {
        this.overviewService = overviewService;
        this.courseReportService = courseReportService;
        this.sectionReportService = sectionReportService;
        this.alertReportService = alertReportService;
    }

    // ------------------------------------------------------------------
    // Integrante 1 - Angel
    // ------------------------------------------------------------------
    @GetMapping("/overview")
    @Operation(summary = "Reporte general del sistema",
            description = "Indicadores globales del dashboard: alertas activas, secciones en "
                    + "alto riesgo, estudiantes en riesgo y tendencia general.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = OverviewResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parametro de filtro invalido",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Error interno",
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

    // ------------------------------------------------------------------
    // Integrante 2 - Adriana
    // ------------------------------------------------------------------
    @GetMapping("/courses/{id}")
    @Operation(summary = "Reporte consolidado de un curso",
            description = "Desempeno general del curso, estudiantes en riesgo y alertas asociadas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = CourseReportResponse.class))),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<CourseReportResponse> getCourseReport(
            @Parameter(description = "ID del curso") @PathVariable Long id,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String teacher,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String alertStatus) {

        ReportFilter filter = new ReportFilter(period, course, teacher, section, riskLevel, alertStatus);
        return ResponseEntity.ok(courseReportService.getCourseReport(id, filter));
    }

    @GetMapping("/sections/{id}")
    @Operation(summary = "Reporte consolidado de una seccion",
            description = "Nivel de riesgo y alertas activas de una seccion especifica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = SectionReportResponse.class))),
            @ApiResponse(responseCode = "404", description = "Seccion no encontrada",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<SectionReportResponse> getSectionReport(
            @Parameter(description = "ID de la seccion") @PathVariable Long id,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String teacher,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String riskLevel,
            @RequestParam(required = false) String alertStatus) {

        ReportFilter filter = new ReportFilter(period, course, teacher, section, riskLevel, alertStatus);
        return ResponseEntity.ok(sectionReportService.getSectionReport(id, filter));
    }

    // ------------------------------------------------------------------
    // Integrante 3 - Allan
    // ------------------------------------------------------------------
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
