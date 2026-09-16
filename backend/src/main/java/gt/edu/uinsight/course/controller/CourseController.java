package gt.edu.uinsight.course.controller;

import gt.edu.uinsight.course.dto.request.CreateCourseRequest;
import gt.edu.uinsight.course.dto.response.CourseResponse;
import gt.edu.uinsight.course.service.CourseService;
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

@Tag(name = "Cursos", description = "Gestion de cursos")
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @Operation(summary = "Crear un curso")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse create(@Valid @RequestBody CreateCourseRequest request) {
        return service.create(request);
    }

    @Operation(summary = "Listar todos los cursos")
    @GetMapping
    public List<CourseResponse> findAll() {
        return service.findAll();
    }

    @Operation(summary = "Obtener un curso por id")
    @GetMapping("/{id}")
    public CourseResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }
}
