package gt.edu.uinsight.alert.b7.controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gt.edu.uinsight.alert.b7.model.RiskInput;
import gt.edu.uinsight.alert.b7.model.RiskOutput;
import gt.edu.uinsight.alert.b7.service.RiskEngineService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class RiskEngineController {

    private final RiskEngineService service;

    public RiskEngineController(RiskEngineService service) {
        this.service = service;
    }

    @PostMapping("/alert/b7/evaluar")
    public RiskOutput evaluar(@RequestBody RiskInput input) {
        return service.evaluarRiesgo(input);
    }
}
