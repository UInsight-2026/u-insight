package gt.edu.uinsight.grade.controller;

import gt.edu.uinsight.grade.dto.BatchGradeResult;
import gt.edu.uinsight.grade.dto.GradeBatchRequest;
import gt.edu.uinsight.grade.dto.GradeRequest;
import gt.edu.uinsight.grade.dto.GradeResponse;
import gt.edu.uinsight.grade.dto.GradeUpdateRequest;
import gt.edu.uinsight.grade.service.GradeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PostMapping("/grades")
    public ResponseEntity<GradeResponse> register(@RequestBody GradeRequest request) {
        GradeResponse response = gradeService.registerGrade(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/grades/batch")
    public ResponseEntity<BatchGradeResult> registerBatch(@RequestBody GradeBatchRequest request) {
        BatchGradeResult result = gradeService.registerBatch(request.getGrades());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/grades/{id}")
    public ResponseEntity<GradeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(gradeService.getById(id));
    }

    @GetMapping("/evaluations/{id}/grades")
    public ResponseEntity<List<GradeResponse>> getByEvaluation(@PathVariable("id") Long evaluationId) {
        return ResponseEntity.ok(gradeService.getByEvaluation(evaluationId));
    }

    @GetMapping("/students/{id}/grades")
    public ResponseEntity<List<GradeResponse>> getByStudent(@PathVariable("id") Long studentId) {
        return ResponseEntity.ok(gradeService.getByStudent(studentId));
    }

    @GetMapping("/sections/{id}/grades")
    public ResponseEntity<List<GradeResponse>> getBySection(@PathVariable("id") Long sectionId) {
        return ResponseEntity.ok(gradeService.getBySection(sectionId));
    }

    @PutMapping("/grades/{id}")
    public ResponseEntity<GradeResponse> update(@PathVariable Long id, @RequestBody GradeUpdateRequest request) {
        return ResponseEntity.ok(gradeService.updateGrade(id, request));
    }
}
