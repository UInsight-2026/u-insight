package gt.edu.uinsight.student.api;

import java.util.Optional;

public interface StudentQueryService {

    Optional<StudentSummary> findSummary(Long studentId);
}
