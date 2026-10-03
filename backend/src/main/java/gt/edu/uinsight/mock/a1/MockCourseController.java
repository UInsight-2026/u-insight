package gt.edu.uinsight.mock.a1;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * MOCK TEMPORAL de A1 (cursos), mientras esa celula fusiona su API real
 * (feature/A1-course) a develop. Replica el contrato publicado en esa rama
 * (CourseResponse: id, code, name, description, credits, status, createdAt,
 * updatedAt). B1 solo usa la existencia (200/404) del recurso, no sus campos.
 *
 * BORRAR ESTE PAQUETE COMPLETO ("gt.edu.uinsight.mock") en cuanto A1 entregue
 * el controller real en gt.edu.uinsight.course.
 */
@RestController
@RequestMapping("/api/v1/courses")
public class MockCourseController {

    private record CourseResponse(Long id, String code, String name, String description,
                                    Integer credits, String status,
                                    LocalDateTime createdAt, LocalDateTime updatedAt) {
    }

    private static CourseResponse course(long id, String code, String name, int credits) {
        LocalDateTime now = LocalDateTime.now();
        return new CourseResponse(id, code, name, "Curso " + name, credits, "ACTIVE", now, now);
    }

    private static final List<CourseResponse> COURSES = List.of(
            course(1, "MAT101", "Matematica Basica", 4),
            course(2, "PRG101", "Programacion I", 5),
            course(3, "BD101", "Bases de Datos", 4),
            course(5, "PRG201", "Programacion II", 5),
            course(9, "EST101", "Estadistica", 3));

    @GetMapping
    public List<CourseResponse> findAll() {
        return COURSES;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> findById(@PathVariable long id) {
        return COURSES.stream().filter(c -> c.id() == id).findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
