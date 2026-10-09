package gt.edu.uinsight.course.dto.response;

import gt.edu.uinsight.course.entity.CourseStatus;

import java.time.LocalDateTime;

/**
 * Incluye el estado del curso para que la celula de secciones pueda aplicar RN-09
 * (no ofrecer cursos INACTIVE para nuevas secciones).
 */
public record CourseResponse(
        Long id,
        String code,
        String name,
        String description,
        Integer credits,
        CourseStatus status,
        LocalDateTime createdAt
) {
}
