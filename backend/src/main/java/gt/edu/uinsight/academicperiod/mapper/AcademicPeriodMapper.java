package gt.edu.uinsight.academicperiod.mapper;

import gt.edu.uinsight.academicperiod.dto.request.CreateAcademicPeriodRequest;
import gt.edu.uinsight.academicperiod.dto.response.AcademicPeriodResponse;
import gt.edu.uinsight.academicperiod.entity.AcademicPeriod;
import gt.edu.uinsight.academicperiod.entity.PeriodStatus;
import org.springframework.stereotype.Component;

@Component
public class AcademicPeriodMapper {

    /** Todo periodo nuevo nace en PLANNED. */
    public AcademicPeriod toEntity(CreateAcademicPeriodRequest request) {
        return new AcademicPeriod(
                request.name().trim(),
                request.year(),
                request.startDate(),
                request.endDate(),
                PeriodStatus.PLANNED
        );
    }

    public AcademicPeriodResponse toResponse(AcademicPeriod entity) {
        return new AcademicPeriodResponse(
                entity.getId(),
                entity.getName(),
                entity.getYear(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
