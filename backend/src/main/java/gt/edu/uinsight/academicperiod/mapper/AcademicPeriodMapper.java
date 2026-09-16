package gt.edu.uinsight.academicperiod.mapper;

import gt.edu.uinsight.academicperiod.dto.response.AcademicPeriodResponse;
import gt.edu.uinsight.academicperiod.entity.AcademicPeriod;
import org.springframework.stereotype.Component;

@Component
public class AcademicPeriodMapper {

    public AcademicPeriodResponse toResponse(AcademicPeriod entity) {
        return new AcademicPeriodResponse(
                entity.getId(),
                entity.getName(),
                entity.getYear(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
