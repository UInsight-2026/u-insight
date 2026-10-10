
package gt.edu.uinsight.analytics.dispersion.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.uinsight.analytics.dispersion.dto.response.DispersionResponse;
import gt.edu.uinsight.analytics.dispersion.service.DispersionService;

@RestController
@RequestMapping("/api/v1/analytics")
public class DispersionController {

    private final DispersionService dispersionService;

    public DispersionController(
            DispersionService dispersionService) {
        this.dispersionService = dispersionService;
    }

    @GetMapping("/sections/{id}/dispersion")
    public ResponseEntity<DispersionResponse> getSectionDispersion(
            @PathVariable("id") Long id) {

        DispersionResponse response =
                dispersionService.getSectionDispersion(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/courses/{id}/dispersion")
    public ResponseEntity<DispersionResponse> getCourseDispersion(
            @PathVariable("id") Long id) {

        DispersionResponse response =
                dispersionService.getCourseDispersion(id);

        return ResponseEntity.ok(response);
    }
}
