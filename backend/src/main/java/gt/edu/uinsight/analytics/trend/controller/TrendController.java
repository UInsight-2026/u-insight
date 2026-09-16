package gt.edu.uinsight.analytics.trend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.service.TrendService;

@RestController 
@RequestMapping("/api/v1/analytics")
public class TrendController {
    
     //servicio
     private final TrendService trendService = null; // Inyectar el servicio real en un escenario de producción

     @GetMapping("/sections/{id}/trend")
     public ResponseEntity<TrendResponse> getTrendBySectionId(@PathVariable Long id) {
         // Lógica para obtener la tendencia por ID de sección
         TrendResponse trendResponse = trendService.getTrendBySectionId(id);
         return ResponseEntity.ok(trendResponse);
     }

    @GetMapping("/students/{id}/trend")
    public ResponseEntity<TrendResponse> getTrendByStudentId(@PathVariable Long id) {
        // Lógica para obtener la tendencia por ID de estudiante
        TrendResponse trendResponse = trendService.getTrendByStudentId(id);
        return ResponseEntity.ok(trendResponse);
    }

}
