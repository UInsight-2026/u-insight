package gt.edu.uinsight.analytics.position.controller;

import gt.edu.uinsight.analytics.position.dto.response.SectionPositionResponse;
import gt.edu.uinsight.analytics.position.dto.response.StudentPositionResponse;
import gt.edu.uinsight.analytics.position.service.PositionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * Endpoints de la celula B2 - Medidas de posicionamiento.
 *
 * GET /api/v1/analytics/sections/{id}/position
 *   -> Q1, Q2, Q3 de la seccion, y percentiles adicionales via ?percentiles=25,50,75,90
 *
 * GET /api/v1/analytics/students/{id}/position
 *   -> percentil del estudiante respecto a su seccion
 */
@RestController
@RequestMapping("/api/v1/analytics")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping("/sections/{id}/position")
    public SectionPositionResponse getSectionPosition(
            @PathVariable Long id,
            @RequestParam(required = false) List<Integer> percentiles) {
        return positionService.getSectionPosition(id, percentiles == null ? Collections.emptyList() : percentiles);
    }

    @GetMapping("/students/{id}/position")
    public StudentPositionResponse getStudentPosition(@PathVariable Long id) {
        return positionService.getStudentPosition(id);
    }
}
