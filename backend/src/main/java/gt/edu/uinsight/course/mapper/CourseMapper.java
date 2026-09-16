package gt.edu.uinsight.course.mapper;

import gt.edu.uinsight.course.dto.response.CourseResponse;
import gt.edu.uinsight.course.entity.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public CourseResponse toResponse(Course entity) {
        return new CourseResponse(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getCredits(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
