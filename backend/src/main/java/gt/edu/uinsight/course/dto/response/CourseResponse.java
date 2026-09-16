package gt.edu.uinsight.course.dto.response;

import gt.edu.uinsight.course.entity.CourseStatus;

import java.time.LocalDateTime;

public record CourseResponse(
        Long id,
        String code,
        String name,
        String description,
        Integer credits,
        CourseStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
