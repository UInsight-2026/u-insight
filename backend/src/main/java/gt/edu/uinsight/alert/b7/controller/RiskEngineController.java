package gt.edu.uinsight.alert.b7.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.uinsight.alert.b7.model.RiskOutput;
import gt.edu.uinsight.alert.b7.service.RiskEngineService;

@RestController
@RequestMapping("/api/v1")
public class RiskEngineController {

    private final RiskEngineService service;

    public RiskEngineController(RiskEngineService service) {
        this.service = service;
    }

    @GetMapping("/alert/b7/evaluar")
    public RiskOutput evaluar() {
        return service.evaluarRiesgo();
    }
}
