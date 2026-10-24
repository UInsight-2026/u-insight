package gt.edu.uinsight.report.controller;

import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.CourseReportResponse;
import gt.edu.uinsight.report.dto.response.SectionReportResponse;
import gt.edu.uinsight.report.exception.ApiError;
import gt.edu.uinsight.report.service.CourseReportService;
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
 * Celula C5 - Reportes de detalle por curso y por seccion.
 */
@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reportes (C5)", description = "Consultas consolidadas para el dashboard de U-Insight")
public class CourseSectionReportController {

    private final CourseReportService courseReportService;
    private final SectionReportService sectionReportService;

    public CourseSectionReportController(CourseReportService courseReportService,
                                          SectionReportService sectionReportService) {
        this.courseReportService = courseReportService;
        this.sectionReportService = sectionReportService;
    }

    @GetMapping("/courses/{id}")
    @Operation(summary = "Reporte consolidado de un curso",
            description = "Desempeno general del curso, estudiantes en riesgo y alertas asociadas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = CourseReportResponse.class))),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado",
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
}
