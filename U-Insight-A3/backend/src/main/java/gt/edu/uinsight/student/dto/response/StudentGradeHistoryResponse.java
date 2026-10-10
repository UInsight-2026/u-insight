package gt.edu.uinsight.student.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Calificación registrada en el historial académico")
public record StudentGradeHistoryResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "8") Long evaluationId,
        @Schema(example = "87.50") BigDecimal score,
        @Schema(example = "2026-09-20T14:30:00") LocalDateTime registeredAt,
        @Schema(example = "ACTIVE") String status
) {
}