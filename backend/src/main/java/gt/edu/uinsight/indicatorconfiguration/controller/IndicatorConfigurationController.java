package gt.edu.uinsight.indicatorconfiguration.controller;

import gt.edu.uinsight.indicatorconfiguration.dto.request.CreateIndicatorConfigurationRequest;
import gt.edu.uinsight.indicatorconfiguration.dto.request.UpdateIndicatorConfigurationRequest;
import gt.edu.uinsight.indicatorconfiguration.dto.response.IndicatorConfigurationResponse;
import gt.edu.uinsight.indicatorconfiguration.service.IndicatorConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/indicator-configurations")
@Tag(name = "C1 - Configuracion de indicadores", description = "Administracion de parametros configurables de U-Insight")
public class IndicatorConfigurationController {

    private final IndicatorConfigurationService service;

    public IndicatorConfigurationController(IndicatorConfigurationService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear una configuracion de indicador")
    public ResponseEntity<IndicatorConfigurationResponse> create(
            @Valid @RequestBody CreateIndicatorConfigurationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping
    @Operation(summary = "Consultar todas las configuraciones de indicadores")
    public ResponseEntity<List<IndicatorConfigurationResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{key}")
    @Operation(summary = "Consultar una configuracion por clave")
    public ResponseEntity<IndicatorConfigurationResponse> findByKey(@PathVariable String key) {
        return ResponseEntity.ok(service.findByKey(key));
    }

    @PutMapping("/{key}")
    @Operation(summary = "Actualizar una configuracion existente")
    public ResponseEntity<IndicatorConfigurationResponse> update(
            @PathVariable String key,
            @Valid @RequestBody UpdateIndicatorConfigurationRequest request
    ) {
        return ResponseEntity.ok(service.update(key, request));
    }
}
