package gt.edu.uinsight.academicperiod.service;

import gt.edu.uinsight.academicperiod.dto.request.CreateAcademicPeriodRequest;
import gt.edu.uinsight.academicperiod.dto.request.UpdateAcademicPeriodRequest;
import gt.edu.uinsight.academicperiod.dto.response.AcademicPeriodResponse;
import gt.edu.uinsight.academicperiod.entity.AcademicPeriod;
import gt.edu.uinsight.academicperiod.entity.PeriodStatus;
import gt.edu.uinsight.academicperiod.mapper.AcademicPeriodMapper;
import gt.edu.uinsight.academicperiod.repository.AcademicPeriodRepository;
import gt.edu.uinsight.academicperiod.support.dto.ChangeStatusRequest;
import gt.edu.uinsight.academicperiod.support.dto.PageResponse;
import gt.edu.uinsight.academicperiod.support.exception.AcademicBusinessRuleException;
import gt.edu.uinsight.academicperiod.support.exception.AcademicResourceNotFoundException;
import gt.edu.uinsight.academicperiod.support.logging.AcademicEventLogger;
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

import java.time.LocalDate;
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
class AcademicPeriodServiceImplTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 15);
    private static final LocalDate END = LocalDate.of(2026, 5, 30);

    @Mock
    private AcademicPeriodRepository repository;

    @Mock
    private AcademicEventLogger eventLogger;

    private AcademicPeriodServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AcademicPeriodServiceImpl(repository, new AcademicPeriodMapper(), eventLogger);
    }

    private static AcademicPeriod period(Long id, String name, PeriodStatus status) {
        AcademicPeriod period = new AcademicPeriod(name, 2026, START, END, status);
        ReflectionTestUtils.setField(period, "id", id);
        return period;
    }

    private static void assertRule(Throwable thrown, String ruleId, HttpStatus status) {
        assertThat(thrown).isInstanceOf(AcademicBusinessRuleException.class);
        AcademicBusinessRuleException ex = (AcademicBusinessRuleException) thrown;
        assertThat(ex.getRuleId()).isEqualTo(ruleId);
        assertThat(ex.getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("create: crea el periodo en estado PLANNED")
    void create_creaPeriodoEnPlanned() {
        when(repository.existsByNameIgnoreCaseAndYear("Primer Semestre", 2026)).thenReturn(false);
        when(repository.save(any(AcademicPeriod.class))).thenAnswer(inv -> {
            AcademicPeriod saved = inv.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", 1L);
            return saved;
        });

        AcademicPeriodResponse response = service.create(
                new CreateAcademicPeriodRequest("  Primer Semestre ", 2026, START, END));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Primer Semestre");
        assertThat(response.status()).isEqualTo(PeriodStatus.PLANNED);
        verify(eventLogger).info(eq("ACADEMIC_PERIOD_CREATED"),
                eq(201), any());
    }

    @Test
    @DisplayName("RN-02: rechaza con 400 si la fecha de inicio es posterior a la de fin")
    void create_rechazaFechasInvertidas() {
        Throwable thrown = catchThrowable(() -> service.create(
                new CreateAcademicPeriodRequest("Primer Semestre", 2026, END, START)));

        assertRule(thrown, "RN-02", HttpStatus.BAD_REQUEST);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("RN-02: rechaza con 400 si la fecha de inicio es igual a la de fin")
    void create_rechazaFechasIguales() {
        Throwable thrown = catchThrowable(() -> service.create(
                new CreateAcademicPeriodRequest("Primer Semestre", 2026, START, START)));

        assertRule(thrown, "RN-02", HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("RN-07: rechaza con 409 un nombre repetido en el mismo anio")
    void create_rechazaNombreDuplicadoEnElAnio() {
        when(repository.existsByNameIgnoreCaseAndYear("Primer Semestre", 2026)).thenReturn(true);

        Throwable thrown = catchThrowable(() -> service.create(
                new CreateAcademicPeriodRequest("Primer Semestre", 2026, START, END)));

        assertRule(thrown, "RN-07", HttpStatus.CONFLICT);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("findById: lanza 404 si el periodo no existe")
    void findById_lanzaNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(AcademicResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("findAll: filtra por estado y pagina")
    void findAll_filtraPorEstado() {
        Pageable pageable = PageRequest.of(0, 20);
        when(repository.findByStatus(PeriodStatus.ACTIVE, pageable))
                .thenReturn(new PageImpl<>(List.of(period(1L, "Primer Semestre", PeriodStatus.ACTIVE)), pageable, 1));

        PageResponse<AcademicPeriodResponse> page = service.findAll("active", pageable);

        assertThat(page.totalElements()).isEqualTo(1);
        assertThat(page.content()).extracting(AcademicPeriodResponse::status).containsExactly(PeriodStatus.ACTIVE);
    }

    @Test
    @DisplayName("findAll: un estado no reconocido responde 400, no se ignora")
    void findAll_rechazaEstadoDesconocido() {
        Throwable thrown = catchThrowable(
                () -> service.findAll("ABIERTO", PageRequest.of(0, 20)));

        assertRule(thrown, null, HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("findActive: lanza 404 si no hay periodo activo")
    void findActive_lanzaNotFound() {
        when(repository.findFirstByStatus(PeriodStatus.ACTIVE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findActive()).isInstanceOf(AcademicResourceNotFoundException.class);
    }

    @Test
    @DisplayName("update: actualiza nombre y fechas de un periodo abierto")
    void update_actualizaPeriodo() {
        AcademicPeriod existing = period(1L, "Primer Semestre", PeriodStatus.PLANNED);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByNameIgnoreCaseAndYearAndIdNot("Semestre I", 2026, 1L)).thenReturn(false);
        when(repository.save(existing)).thenReturn(existing);

        AcademicPeriodResponse response = service.update(1L,
                new UpdateAcademicPeriodRequest("Semestre I", START.plusDays(5), END.plusDays(5)));

        assertThat(response.name()).isEqualTo("Semestre I");
        assertThat(response.startDate()).isEqualTo(START.plusDays(5));
    }

    @Test
    @DisplayName("RN-04: un periodo CLOSED no acepta modificaciones (409)")
    void update_rechazaPeriodoCerrado() {
        when(repository.findById(1L)).thenReturn(Optional.of(period(1L, "Primer Semestre", PeriodStatus.CLOSED)));

        Throwable thrown = catchThrowable(() -> service.update(1L,
                new UpdateAcademicPeriodRequest("Otro", START, END)));

        assertRule(thrown, "RN-04", HttpStatus.CONFLICT);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("RN-07: al actualizar, rechaza un nombre que ya usa otro periodo del mismo anio")
    void update_rechazaNombreDuplicado() {
        when(repository.findById(1L)).thenReturn(Optional.of(period(1L, "Primer Semestre", PeriodStatus.PLANNED)));
        when(repository.existsByNameIgnoreCaseAndYearAndIdNot("Segundo Semestre", 2026, 1L)).thenReturn(true);

        Throwable thrown = catchThrowable(() -> service.update(1L,
                new UpdateAcademicPeriodRequest("Segundo Semestre", START, END)));

        assertRule(thrown, "RN-07", HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("changeStatus: PLANNED -> ACTIVE cuando no hay otro activo")
    void changeStatus_activaPeriodo() {
        AcademicPeriod existing = period(1L, "Primer Semestre", PeriodStatus.PLANNED);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByStatus(PeriodStatus.ACTIVE)).thenReturn(false);
        when(repository.save(existing)).thenReturn(existing);

        AcademicPeriodResponse response = service.changeStatus(1L, new ChangeStatusRequest("ACTIVE"));

        assertThat(response.status()).isEqualTo(PeriodStatus.ACTIVE);
        verify(eventLogger).info(eq("ACADEMIC_PERIOD_ACTIVATED"),
                eq(200), any());
    }

    @Test
    @DisplayName("changeStatus: ACTIVE -> CLOSED")
    void changeStatus_cierraPeriodo() {
        AcademicPeriod existing = period(1L, "Primer Semestre", PeriodStatus.ACTIVE);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        AcademicPeriodResponse response = service.changeStatus(1L, new ChangeStatusRequest("CLOSED"));

        assertThat(response.status()).isEqualTo(PeriodStatus.CLOSED);
    }

    @Test
    @DisplayName("RN-03: no permite un segundo periodo ACTIVE (409)")
    void changeStatus_rechazaSegundoActivo() {
        when(repository.findById(2L)).thenReturn(Optional.of(period(2L, "Segundo Semestre", PeriodStatus.PLANNED)));
        when(repository.existsByStatus(PeriodStatus.ACTIVE)).thenReturn(true);

        Throwable thrown = catchThrowable(
                () -> service.changeStatus(2L, new ChangeStatusRequest("ACTIVE")));

        assertRule(thrown, "RN-03", HttpStatus.CONFLICT);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("RN-06: no permite saltar de PLANNED a CLOSED (409)")
    void changeStatus_rechazaSaltoDeEstado() {
        when(repository.findById(1L)).thenReturn(Optional.of(period(1L, "Primer Semestre", PeriodStatus.PLANNED)));

        Throwable thrown = catchThrowable(
                () -> service.changeStatus(1L, new ChangeStatusRequest("CLOSED")));

        assertRule(thrown, "RN-06", HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("RN-04/RN-06: no permite reabrir un periodo CLOSED (409)")
    void changeStatus_rechazaReabrirCerrado() {
        when(repository.findById(1L)).thenReturn(Optional.of(period(1L, "Primer Semestre", PeriodStatus.CLOSED)));

        Throwable thrown = catchThrowable(
                () -> service.changeStatus(1L, new ChangeStatusRequest("ACTIVE")));

        assertRule(thrown, "RN-04", HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("changeStatus: un estado no reconocido responde 400")
    void changeStatus_rechazaEstadoDesconocido() {
        Throwable thrown = catchThrowable(
                () -> service.changeStatus(1L, new ChangeStatusRequest("FINISHED")));

        assertRule(thrown, null, HttpStatus.BAD_REQUEST);
        verify(repository, never()).findById(any());
    }
}
