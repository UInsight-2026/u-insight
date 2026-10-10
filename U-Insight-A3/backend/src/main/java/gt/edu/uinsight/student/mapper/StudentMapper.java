package gt.edu.uinsight.student.mapper;

import gt.edu.uinsight.grade.entity.Grade;
import gt.edu.uinsight.student.dto.response.StudentAcademicHistoryResponse;
import gt.edu.uinsight.student.dto.response.StudentGradeHistoryResponse;
import gt.edu.uinsight.student.dto.response.StudentResponse;
import gt.edu.uinsight.student.entity.Student;

import java.util.List;

public final class StudentMapper {

    private StudentMapper() {
    }

    public static StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getStudentCode(),
                student.getStudentName(),
                student.getEmail(),
                student.getStatus()
        );
    }

    public static StudentGradeHistoryResponse toGradeHistoryResponse(Grade grade) {
        return new StudentGradeHistoryResponse(
                grade.getId(),
                grade.getEvaluationId(),
                grade.getScore(),
                grade.getRegisteredAt(),
                grade.getStatus()
        );
    }

    public static StudentAcademicHistoryResponse toAcademicHistory(
            Student student,
            List<Grade> grades) {
        return new StudentAcademicHistoryResponse(
                student.getId(),
                student.getStudentCode(),
                student.getStudentName(),
                grades.stream().map(StudentMapper::toGradeHistoryResponse).toList()
        );
    }
}