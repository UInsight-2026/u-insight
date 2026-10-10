package gt.edu.uinsight.course.mapper;

import gt.edu.uinsight.course.dto.request.CreateCourseRequest;
import gt.edu.uinsight.course.dto.response.CourseResponse;
import gt.edu.uinsight.course.entity.Course;
import gt.edu.uinsight.course.entity.CourseStatus;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    /** Todo curso nuevo nace en ACTIVE. */
    public Course toEntity(CreateCourseRequest request) {
        return new Course(
                request.code().trim(),
                request.name().trim(),
                request.description(),
                request.credits(),
                CourseStatus.ACTIVE
        );
    }

    public CourseResponse toResponse(Course entity) {
        return new CourseResponse(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getCredits(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
