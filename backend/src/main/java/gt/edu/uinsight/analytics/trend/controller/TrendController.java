package gt.edu.uinsight.analytics.trend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.uinsight.analytics.trend.dto.response.TrendResponse;
import gt.edu.uinsight.analytics.trend.service.TrendService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Positive;

@RestController 
@RequestMapping("/api/v1/analytics")
@Validated
public class TrendController {
    
    private final TrendService trendService;

    public TrendController(TrendService trendService) {
        this.trendService = trendService;
    }

     @GetMapping("/sections/{id}/trends")
     public ResponseEntity<TrendResponse> getTrendBySectionId(@PathVariable @Positive Long id) {
         // Lógica para obtener la tendencia por ID de sección
         TrendResponse trendResponse = trendService.getTrendBySectionId(id);
         if(trendResponse == null){
             throw new EntityNotFoundException("La sección con id " + id + " no existe.");
         }
         return ResponseEntity.ok(trendResponse);
     }

    @GetMapping("/students/{id}/trends")
    public ResponseEntity<TrendResponse> getTrendByStudentId(@PathVariable @Positive Long id) {
        // Lógica para obtener la tendencia por ID de estudiante
        TrendResponse trendResponse = trendService.getTrendByStudentId(id);
        if(trendResponse == null){
            throw new EntityNotFoundException("El estudiante con id " + id + " no existe.");
        }
        return ResponseEntity.ok(trendResponse);
    }

}