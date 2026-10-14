// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.followup.service;

import gt.edu.uinsight.followup.dto.request.CreateFollowUpRequest;
import gt.edu.uinsight.followup.dto.response.FollowUpResponse;
import gt.edu.uinsight.followup.entity.FollowUp;
import gt.edu.uinsight.followup.entity.FollowUpResult;
import gt.edu.uinsight.followup.exception.InterventionNotActiveException;
import gt.edu.uinsight.followup.exception.InvalidFollowUpDateException;
import gt.edu.uinsight.followup.repository.FollowUpRepository;
import gt.edu.uinsight.intervention.entity.Intervention;
import gt.edu.uinsight.intervention.entity.InterventionStatus;
import gt.edu.uinsight.intervention.entity.InterventionType;
import gt.edu.uinsight.intervention.exception.InterventionNotFoundException;
import gt.edu.uinsight.intervention.repository.InterventionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FollowUpServiceTest {

    @Mock
    private FollowUpRepository followUpRepository;

    @Mock
    private InterventionRepository interventionRepository;

    private FollowUpService service;

    @BeforeEach
    void setUp() {
        service = new FollowUpService(followUpRepository, interventionRepository);
    }

    private Intervention interventionWithStatus(Long id, InterventionStatus status, LocalDate startDate) {
        Intervention intervention = new Intervention(10L, InterventionType.TUTORING,
                "Tutoría de refuerzo", "Lic. Ana Pérez", startDate);
        intervention.changeStatus(status);
        setId(intervention, id);
        return intervention;
    }

    private void setId(Intervention intervention, Long id) {
        try {
            Field field = Intervention.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(intervention, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private CreateFollowUpRequest requestOn(LocalDate date) {
        return new CreateFollowUpRequest(date, "El estudiante mejoró su asistencia", FollowUpResult.IMPROVED);
    }

    @Test
    void create_debeRegistrarSeguimiento_cuandoIntervencionEstaPlanned() {
        Intervention intervention = interventionWithStatus(4L, InterventionStatus.PLANNED,
                LocalDate.of(2026, 9, 1));
        when(interventionRepository.findById(4L)).thenReturn(Optional.of(intervention));
        when(followUpRepository.save(any(FollowUp.class))).thenAnswer(inv -> {
            FollowUp followUp = inv.getArgument(0);
            Field idField;
            try {
                idField = FollowUp.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(followUp, 1L);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            return followUp;
        });

        FollowUpResponse response = service.create(4L, requestOn(LocalDate.of(2026, 9, 10)));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.interventionId()).isEqualTo(4L);
        assertThat(response.result()).isEqualTo(FollowUpResult.IMPROVED);
    }

    @Test
    void create_debeRegistrarSeguimiento_cuandoIntervencionEstaInProgress() {
        Intervention intervention = interventionWithStatus(4L, InterventionStatus.IN_PROGRESS,
                LocalDate.of(2026, 9, 1));
        when(interventionRepository.findById(4L)).thenReturn(Optional.of(intervention));
        when(followUpRepository.save(any(FollowUp.class))).thenAnswer(inv -> inv.getArgument(0));

        FollowUpResponse response = service.create(4L, requestOn(LocalDate.of(2026, 9, 10)));

        assertThat(response.interventionId()).isEqualTo(4L);
    }

    @Test
    void create_debeLanzarInterventionNotFound_cuandoLaIntervencionNoExiste() {
        when(interventionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(99L, requestOn(LocalDate.now())))
                .isInstanceOf(InterventionNotFoundException.class);
    }

    @Test
    void create_debeRechazar_cuandoLaIntervencionEstaCompletada() {
        Intervention intervention = interventionWithStatus(4L, InterventionStatus.COMPLETED,
                LocalDate.of(2026, 9, 1));
        when(interventionRepository.findById(4L)).thenReturn(Optional.of(intervention));

        assertThatThrownBy(() -> service.create(4L, requestOn(LocalDate.of(2026, 9, 10))))
                .isInstanceOf(InterventionNotActiveException.class);
    }

    @Test
    void create_debeRechazar_cuandoLaIntervencionEstaCancelada() {
        Intervention intervention = interventionWithStatus(4L, InterventionStatus.CANCELLED,
                LocalDate.of(2026, 9, 1));
        when(interventionRepository.findById(4L)).thenReturn(Optional.of(intervention));

        assertThatThrownBy(() -> service.create(4L, requestOn(LocalDate.of(2026, 9, 10))))
                .isInstanceOf(InterventionNotActiveException.class);
    }

    @Test
    void create_debeRechazar_cuandoFollowUpDateEsAnteriorAlStartDate() {
        Intervention intervention = interventionWithStatus(4L, InterventionStatus.PLANNED,
                LocalDate.of(2026, 9, 10));
        when(interventionRepository.findById(4L)).thenReturn(Optional.of(intervention));

        assertThatThrownBy(() -> service.create(4L, requestOn(LocalDate.of(2026, 9, 1))))
                .isInstanceOf(InvalidFollowUpDateException.class);
    }

    @Test
    void create_debePermitir_cuandoFollowUpDateEsIgualAlStartDate() {
        Intervention intervention = interventionWithStatus(4L, InterventionStatus.PLANNED,
                LocalDate.of(2026, 9, 10));
        when(interventionRepository.findById(4L)).thenReturn(Optional.of(intervention));
        when(followUpRepository.save(any(FollowUp.class))).thenAnswer(inv -> inv.getArgument(0));

        FollowUpResponse response = service.create(4L, requestOn(LocalDate.of(2026, 9, 10)));

        assertThat(response.followUpDate()).isEqualTo(LocalDate.of(2026, 9, 10));
    }

    @Test
    void listByIntervention_debeRetornarSoloLosNoEliminados_ordenadosPorFecha() {
        when(interventionRepository.findById(4L)).thenReturn(
                Optional.of(interventionWithStatus(4L, InterventionStatus.PLANNED, LocalDate.of(2026, 9, 1))));
        FollowUp f1 = new FollowUp(4L, LocalDate.of(2026, 9, 5), "Primer seguimiento", FollowUpResult.PENDING);
        when(followUpRepository.findByInterventionIdAndDeletedFalseOrderByFollowUpDateAsc(4L))
                .thenReturn(List.of(f1));

        List<FollowUpResponse> result = service.listByIntervention(4L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).observation()).isEqualTo("Primer seguimiento");
    }

    @Test
    void listByIntervention_debeLanzarInterventionNotFound_cuandoLaIntervencionNoExiste() {
        when(interventionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.listByIntervention(99L))
                .isInstanceOf(InterventionNotFoundException.class);
    }

    @Test
    void listByIntervention_debeRetornarListaVacia_cuandoNoHaySeguimientos() {
        when(interventionRepository.findById(4L)).thenReturn(
                Optional.of(interventionWithStatus(4L, InterventionStatus.PLANNED, LocalDate.of(2026, 9, 1))));
        when(followUpRepository.findByInterventionIdAndDeletedFalseOrderByFollowUpDateAsc(4L))
                .thenReturn(List.of());

        List<FollowUpResponse> result = service.listByIntervention(4L);

        assertThat(result).isEmpty();
    }
}
