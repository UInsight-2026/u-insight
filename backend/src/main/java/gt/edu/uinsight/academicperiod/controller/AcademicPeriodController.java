package gt.edu.uinsight.academicperiod.controller;

import gt.edu.uinsight.academicperiod.dto.request.CreateAcademicPeriodRequest;
import gt.edu.uinsight.academicperiod.dto.response.AcademicPeriodResponse;
import gt.edu.uinsight.academicperiod.service.AcademicPeriodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Periodos academicos", description = "Gestion de periodos academicos")
@RestController
@RequestMapping("/api/v1/academic-periods")
public class AcademicPeriodController {

    private final AcademicPeriodService service;

    public AcademicPeriodController(AcademicPeriodService service) {
        this.service = service;
    }

    @Operation(summary = "Crear un periodo academico")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AcademicPeriodResponse create(@Valid @RequestBody CreateAcademicPeriodRequest request) {
        return service.create(request);
    }

    @Operation(summary = "Listar todos los periodos academicos")
    @GetMapping
    public List<AcademicPeriodResponse> findAll() {
        return service.findAll();
    }

    @Operation(summary = "Obtener un periodo academico por id")
    @GetMapping("/{id}")
    public AcademicPeriodResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }
}
