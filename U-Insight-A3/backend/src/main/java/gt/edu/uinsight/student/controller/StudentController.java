package gt.edu.uinsight.student.controller;

import gt.edu.uinsight.student.dto.request.CreateStudentRequest;
import gt.edu.uinsight.student.dto.request.UpdateStudentStatusRequest;
import gt.edu.uinsight.student.dto.response.StudentAcademicHistoryResponse;
import gt.edu.uinsight.student.dto.response.StudentResponse;
import gt.edu.uinsight.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@Tag(name = "Estudiantes", description = "Registro y consulta de estudiantes anonimizados")
@Validated
@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(summary = "Registrar un estudiante", description = "Registra datos ficticios o anonimizados.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Estudiante creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Código de estudiante duplicado")
    })
    @PostMapping
    public ResponseEntity<StudentResponse> create(@Valid @RequestBody CreateStudentRequest request) {
        StudentResponse response = studentService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/students/" + response.id()))
                .body(response);
    }

    @Operation(summary = "Listar estudiantes")
    @ApiResponse(responseCode = "200", description = "Listado de estudiantes")
    @GetMapping
    public ResponseEntity<List<StudentResponse>> list() {
        return ResponseEntity.ok(studentService.findAll());
    }

    @Operation(summary = "Consultar estudiante por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estudiante encontrado"),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getById(
            @Parameter(description = "ID del estudiante", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(studentService.findById(id));
    }

    @Operation(summary = "Consultar estudiante por código anónimo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estudiante encontrado"),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @GetMapping("/code/{code}")
    public ResponseEntity<StudentResponse> getByCode(
            @Parameter(description = "Código anónimo", example = "EST-0001") @PathVariable String code) {
        return ResponseEntity.ok(studentService.findByCode(code));
    }

    @Operation(summary = "Cambiar estado del estudiante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<StudentResponse> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentStatusRequest request) {
        return ResponseEntity.ok(studentService.changeStatus(id, request.status()));
    }

    @Operation(summary = "Consultar historial académico del estudiante",
            description = "Devuelve las calificaciones relacionadas con el estudiante; sin notas, grades es una lista vacía.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial obtenido"),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @GetMapping("/{id}/academic-history")
    public ResponseEntity<StudentAcademicHistoryResponse> academicHistory(
            @PathVariable Long id) {
        return ResponseEntity.ok(studentService.academicHistory(id));
    }
}