package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.mock.model.MockAlert;
import gt.edu.uinsight.report.mock.model.MockCourse;
import gt.edu.uinsight.report.mock.model.MockSection;
import java.util.List;
import java.util.Optional;

/** Contrato de lectura de C5; permite sustituir los datos simulados. */
public interface ReportDataGateway {
    List<MockCourse> findCourses(ReportFilter filter);
    Optional<MockCourse> findCourseById(Long id);
    List<MockSection> findSections(ReportFilter filter);
    Optional<MockSection> findSectionById(Long id);
    List<MockSection> findSectionsByCourseId(Long courseId);
    List<MockAlert> findAlerts(ReportFilter filter);
    List<MockAlert> findAlertsBySectionId(Long sectionId);
    List<MockAlert> findAlertsByCourseId(Long courseId);
}
