package gt.edu.uinsight.student.service;

import gt.edu.uinsight.grade.entity.Grade;
import gt.edu.uinsight.grade.repository.GradeRepository;
import gt.edu.uinsight.student.dto.request.CreateStudentRequest;
import gt.edu.uinsight.student.dto.response.StudentAcademicHistoryResponse;
import gt.edu.uinsight.student.dto.response.StudentResponse;
import gt.edu.uinsight.student.entity.Student;
import gt.edu.uinsight.student.entity.StudentStatus;
import gt.edu.uinsight.student.exception.DuplicateStudentCodeException;
import gt.edu.uinsight.student.exception.InactiveStudentException;
import gt.edu.uinsight.student.exception.StudentNotFoundException;
import gt.edu.uinsight.student.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private StudentService studentService;

    private CreateStudentRequest validRequest() {
        return new CreateStudentRequest(
                "est-0001",
                " Estudiante 0001 ",
                "estudiante0001@example.test"
        );
    }

    @Test
    void create_normalizaCodigoYNombresYAsignaIdentificador() {
        when(studentRepository.existsByStudentCodeIgnoreCase("EST-0001")).thenReturn(false);
        when(studentRepository.saveAndFlush(any(Student.class))).thenAnswer(invocation -> {
            Student student = invocation.getArgument(0);
            student.setId(1L);
            return student;
        });

        StudentResponse response = studentService.create(validRequest());

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.studentCode()).isEqualTo("EST-0001");
        assertThat(response.studentName()).isEqualTo("Estudiante 0001");
        assertThat(response.status()).isEqualTo(StudentStatus.ACTIVE);
    }

    @Test
    void create_rechazaCodigoDuplicado() {
        when(studentRepository.existsByStudentCodeIgnoreCase("EST-0001")).thenReturn(true);

        assertThatThrownBy(() -> studentService.create(validRequest()))
                .isInstanceOf(DuplicateStudentCodeException.class);
    }

    @Test
    void findById_lanza404DeDominioCuandoNoExiste() {
        when(studentRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.findById(404L))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    void changeStatus_actualizaEstado() {
        Student student = new Student("EST-0001", "Estudiante 0001", null);
        student.setId(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(any(Student.class))).thenAnswer(invocation -> invocation.getArgument(0));

        StudentResponse response = studentService.changeStatus(1L, StudentStatus.INACTIVE);

        assertThat(response.status()).isEqualTo(StudentStatus.INACTIVE);
        verify(studentRepository).save(student);
    }

    @Test
    void academicHistory_devuelveCalificacionesDelEstudiante() {
        Student student = new Student("EST-0001", "Estudiante 0001", null);
        student.setId(1L);
        Grade grade = new Grade(8L, student, new BigDecimal("87.50"), "ACTIVE");
        grade.setId(20L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(gradeRepository.findByStudent_IdOrderByRegisteredAtDescIdDesc(1L))
                .thenReturn(List.of(grade));

        StudentAcademicHistoryResponse response = studentService.academicHistory(1L);

        assertThat(response.studentCode()).isEqualTo("EST-0001");
        assertThat(response.grades()).hasSize(1);
        assertThat(response.grades().get(0).evaluationId()).isEqualTo(8L);
        assertThat(response.grades().get(0).score()).isEqualByComparingTo("87.50");
    }

    @Test
    void academicHistory_sinCalificacionesDevuelveListaVacia() {
        Student student = new Student("EST-0001", "Estudiante 0001", null);
        student.setId(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(gradeRepository.findByStudent_IdOrderByRegisteredAtDescIdDesc(1L))
                .thenReturn(List.of());

        assertThat(studentService.academicHistory(1L).grades()).isEmpty();
    }

    @Test
    void requireActiveForEnrollment_rechazaEstudianteInactivo() {
        Student student = new Student("EST-0001", "Estudiante 0001", null);
        student.setId(1L);
        student.setStatus(StudentStatus.INACTIVE);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        assertThatThrownBy(() -> studentService.requireActiveForEnrollment(1L))
                .isInstanceOf(InactiveStudentException.class);
    }
}