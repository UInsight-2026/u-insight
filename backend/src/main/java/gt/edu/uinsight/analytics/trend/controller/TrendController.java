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

    // Parche de arranque aportado por C7: esta ruta era identica a la
    // GET /api/v1/analytics/students/{id}/trend que la celula B5 ya entrego en la semana 2,
    // y Spring se negaba a levantar por mapeo ambiguo. Se renombra la de B4 para que ambas
    // convivan; no se borro nada.
    // Pendiente: que B4 y B5 acuerden cual de las dos queda y con que ruta definitiva.
    // Aviso aparte: trendService esta fijado a null mas arriba, asi que estos dos endpoints
    // responden 500 aunque arranquen. Es codigo sin terminar de B4, no lo causo este parche.
    @GetMapping("/students/{id}/trend-analysis")
    public ResponseEntity<TrendResponse> getTrendByStudentId(@PathVariable Long id) {
        // Lógica para obtener la tendencia por ID de estudiante
        TrendResponse trendResponse = trendService.getTrendByStudentId(id);
        return ResponseEntity.ok(trendResponse);
    }

}
