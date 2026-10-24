package gt.edu.uinsight.student.service;

import gt.edu.uinsight.student.api.StudentQueryService;
import gt.edu.uinsight.student.api.StudentSummary;
import gt.edu.uinsight.student.repository.StudentRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class StudentQueryServiceImpl implements StudentQueryService {

    private final StudentRecordRepository repository;

    public StudentQueryServiceImpl(StudentRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentSummary> findSummary(Long studentId) {
        return repository.findById(studentId)
                .map(student -> new StudentSummary(student.getId(), student.getStudentCode()));
    }
}
