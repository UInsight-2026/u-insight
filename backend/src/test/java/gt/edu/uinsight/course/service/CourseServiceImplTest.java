package gt.edu.uinsight.course.service;

import gt.edu.uinsight.academicperiod.support.dto.ChangeStatusRequest;
import gt.edu.uinsight.academicperiod.support.dto.PageResponse;
import gt.edu.uinsight.academicperiod.support.exception.AcademicBusinessRuleException;
import gt.edu.uinsight.academicperiod.support.exception.AcademicResourceNotFoundException;
import gt.edu.uinsight.academicperiod.support.logging.AcademicEventLogger;
import gt.edu.uinsight.course.dto.request.CreateCourseRequest;
import gt.edu.uinsight.course.dto.request.UpdateCourseRequest;
import gt.edu.uinsight.course.dto.response.CourseResponse;
import gt.edu.uinsight.course.entity.Course;
import gt.edu.uinsight.course.entity.CourseStatus;
import gt.edu.uinsight.course.mapper.CourseMapper;
import gt.edu.uinsight.course.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository repository;

    @Mock
    private AcademicEventLogger eventLogger;

    private CourseServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CourseServiceImpl(repository, new CourseMapper(), eventLogger);
    }

    private static Course course(Long id, String code, CourseStatus status) {
        Course course = new Course(code, "Programacion II", "POO en Java", 5, status);
        ReflectionTestUtils.setField(course, "id", id);
        return course;
    }

    private static void assertRule(Throwable thrown, String ruleId, HttpStatus status) {
        assertThat(thrown).isInstanceOf(AcademicBusinessRuleException.class);
        AcademicBusinessRuleException ex = (AcademicBusinessRuleException) thrown;
        assertThat(ex.getRuleId()).isEqualTo(ruleId);
        assertThat(ex.getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("create: crea el curso en estado ACTIVE")
    void create_creaCursoActivo() {
        when(repository.existsByCodeIgnoreCase("PROG-II")).thenReturn(false);
        when(repository.save(any(Course.class))).thenAnswer(inv -> {
            Course saved = inv.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 7L);
            return saved;
        });

        CourseResponse response = service.create(
                new CreateCourseRequest(" PROG-II ", "Programacion II", "Curso de POO y APIs REST", 5));

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.code()).isEqualTo("PROG-II");
        assertThat(response.status()).isEqualTo(CourseStatus.ACTIVE);
        verify(eventLogger).info(eq("COURSE_CREATED"), eq(201), any());
    }

    @Test
    @DisplayName("create: los creditos son opcionales")
    void create_permiteCreditosNulos() {
        when(repository.existsByCodeIgnoreCase("ETICA")).thenReturn(false);
        when(repository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        CourseResponse response = service.create(new CreateCourseRequest("ETICA", "Etica", null, null));

        assertThat(response.credits()).isNull();
    }

    @Test
    @DisplayName("RN-01: rechaza con 409 un codigo repetido sin distinguir mayusculas")
    void create_rechazaCodigoDuplicado() {
        when(repository.existsByCodeIgnoreCase("prog2")).thenReturn(true);

        Throwable thrown = catchThrowable(() -> service.create(
                new CreateCourseRequest("prog2", "Duplicado", null, 3)));

        assertRule(thrown, "RN-01", HttpStatus.CONFLICT);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("RN-08: rechaza con 400 creditos iguales a cero")
    void create_rechazaCreditosCero() {
        Throwable thrown = catchThrowable(() -> service.create(
                new CreateCourseRequest("EST1", "Estadistica", null, 0)));

        assertRule(thrown, "RN-08", HttpStatus.BAD_REQUEST);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("RN-08: rechaza con 400 creditos negativos al actualizar")
    void update_rechazaCreditosNegativos() {
        when(repository.findById(1L)).thenReturn(Optional.of(course(1L, "PROG2", CourseStatus.ACTIVE)));

        Throwable thrown = catchThrowable(() -> service.update(1L,
                new UpdateCourseRequest("Programacion II", null, -2)));

        assertRule(thrown, "RN-08", HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("findById: lanza 404 si el curso no existe")
    void findById_lanzaNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L)).isInstanceOf(AcademicResourceNotFoundException.class);
    }

    @Test
    @DisplayName("findByCode: busca sin distinguir mayusculas")
    void findByCode_encuentraSinDistinguirMayusculas() {
        when(repository.findByCodeIgnoreCase("prog2")).thenReturn(Optional.of(course(2L, "PROG2", CourseStatus.ACTIVE)));

        assertThat(service.findByCode("prog2").code()).isEqualTo("PROG2");
    }

    @Test
    @DisplayName("findByCode: lanza 404 si el codigo no existe")
    void findByCode_lanzaNotFound() {
        when(repository.findByCodeIgnoreCase("XYZ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByCode("XYZ")).isInstanceOf(AcademicResourceNotFoundException.class);
    }

    @Test
    @DisplayName("findAll: filtra por estado y pagina")
    void findAll_filtraPorEstado() {
        Pageable pageable = PageRequest.of(0, 20);
        when(repository.findByStatus(CourseStatus.INACTIVE, pageable))
                .thenReturn(new PageImpl<>(List.of(course(3L, "BD1", CourseStatus.INACTIVE)), pageable, 1));

        PageResponse<CourseResponse> page = service.findAll("INACTIVE", pageable);

        assertThat(page.content()).extracting(CourseResponse::status).containsExactly(CourseStatus.INACTIVE);
    }

    @Test
    @DisplayName("findAll: un estado no reconocido responde 400")
    void findAll_rechazaEstadoDesconocido() {
        Throwable thrown = catchThrowable(() -> service.findAll("BORRADO", PageRequest.of(0, 20)));

        assertRule(thrown, null, HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("update: actualiza nombre, descripcion y creditos sin cambiar el codigo")
    void update_actualizaCurso() {
        Course existing = course(1L, "PROG2", CourseStatus.ACTIVE);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        CourseResponse response = service.update(1L, new UpdateCourseRequest("Programacion 2", "Nueva", 4));

        assertThat(response.code()).isEqualTo("PROG2");
        assertThat(response.name()).isEqualTo("Programacion 2");
        assertThat(response.credits()).isEqualTo(4);
        verify(eventLogger).info(eq("COURSE_UPDATED"), eq(200), any());
    }

    @Test
    @DisplayName("RN-05/RN-09: la baja es logica y el estado INACTIVE queda expuesto en la respuesta")
    void changeStatus_desactivaCurso() {
        Course existing = course(1L, "PROG2", CourseStatus.ACTIVE);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        CourseResponse response = service.changeStatus(1L, new ChangeStatusRequest("inactive"));

        assertThat(response.status()).isEqualTo(CourseStatus.INACTIVE);
        verify(repository, never()).delete(any());
        verify(eventLogger).info(eq("COURSE_STATUS_CHANGED"), eq(200), any());
    }

    @Test
    @DisplayName("changeStatus: rechaza con 409 cambiar al mismo estado")
    void changeStatus_rechazaMismoEstado() {
        when(repository.findById(1L)).thenReturn(Optional.of(course(1L, "PROG2", CourseStatus.ACTIVE)));

        Throwable thrown = catchThrowable(() -> service.changeStatus(1L, new ChangeStatusRequest("ACTIVE")));

        assertRule(thrown, "RN-06", HttpStatus.CONFLICT);
    }
}
