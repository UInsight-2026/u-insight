package gt.edu.uinsight.student.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Estudiante y calificaciones registradas")
public record StudentAcademicHistoryResponse(
        @Schema(example = "1") Long studentId,
        @Schema(example = "EST-0001") String studentCode,
        @Schema(example = "Estudiante 0001") String studentName,
        List<StudentGradeHistoryResponse> grades
) {
}