package gt.edu.uinsight.report.controller;

//semana 3
import gt.edu.uinsight.report.dto.filter.PageFilter;


import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.dto.response.AlertReportResponse;
import gt.edu.uinsight.report.exception.ApiError;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Celula C5 - Listado consolidado de alertas.
 */
@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reportes (C5)", description = "Consultas consolidadas para el dashboard de U-Insight")
public class AlertReportController {

    private final AlertReportService alertReportService;

    public AlertReportController(AlertReportService alertReportService) {
        this.alertReportService = alertReportService;
    }

   //semana 3
   @GetMapping("/alerts")
    @Operation(summary = "Listado consolidado de alertas",
            description = "Alertas generadas en la plataforma, con filtros, orden y paginación.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = AlertReportResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parámetro de filtro o de paginación inválido",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<AlertReportResponse> getAlerts(
            @Parameter(description = "Período académico, ej. 2026-2") 
            @RequestParam(required = false) String period,
            
            @RequestParam(required = false) String course,
            @RequestParam(required = false) String teacher,
            @RequestParam(required = false) String section,
            
            @Parameter(description = "Nivel de riesgo: LOW, MEDIUM, HIGH") 
            @RequestParam(required = false) String riskLevel,
            
            @Parameter(description = "Estado: NEW, UNDER_REVIEW, IN_PROGRESS, RESOLVED, DISMISSED") 
            @RequestParam(required = false) String alertStatus,
            
            @Parameter(description = "Número de página, empieza en 0") 
            @RequestParam(required = false) Integer page,
            
            @Parameter(description = "Tamaño de página, entre 1 y 100") 
            @RequestParam(required = false) Integer size,
            
            @Parameter(description = "Orden: NEWEST, OLDEST, RISK") 
            @RequestParam(required = false) String sort) {

        ReportFilter filter = new ReportFilter(period, course, teacher, section, riskLevel, alertStatus);
        PageFilter pageFilter = new PageFilter(page, size, sort);
        
        return ResponseEntity.ok(alertReportService.getAlerts(filter, pageFilter));
    }
}
