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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @InjectMocks
    private FollowUpService followUpService;

    /**
     * Intervention no expone setters (mutación solo vía JPA, por diseño del módulo
     * intervention). Se usa reflexión únicamente para simular en el test estados
     * distintos al default (PLANNED) que fija su constructor público.
     */
    private Intervention intervention(InterventionStatus status, LocalDate startDate) {
        Intervention entity = new Intervention(10L, InterventionType.TUTORING,
                "Tutoría de refuerzo en matemática", "Lic. Ana Pérez", startDate);
        if (status != InterventionStatus.PLANNED) {
            try {
                var field = Intervention.class.getDeclaredField("status");
                field.setAccessible(true);
                field.set(entity, status);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        return entity;
    }

    private CreateFollowUpRequest validRequest(LocalDate followUpDate) {
        return new CreateFollowUpRequest(followUpDate, "El estudiante mostró mejoría", FollowUpResult.IMPROVED);
    }

    @Test
    void create_debeRegistrarFollowUp_cuandoIntervencionExisteYEstaActiva() {
        Long interventionId = 5L;
        LocalDate startDate = LocalDate.of(2026, 9, 1);
        Intervention intervention = intervention(InterventionStatus.PLANNED, startDate);

        when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(followUpRepository.save(any(FollowUp.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FollowUpResponse response = followUpService.create(interventionId, validRequest(startDate.plusDays(1)));

        assertThat(response.interventionId()).isEqualTo(interventionId);
        assertThat(response.result()).isEqualTo(FollowUpResult.IMPROVED);
    }

    @Test
    void create_debeLanzarInterventionNotFound_cuandoNoExisteIntervencion() {
        Long interventionId = 999L;
        when(interventionRepository.findById(interventionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followUpService.create(interventionId, validRequest(LocalDate.now())))
                .isInstanceOf(InterventionNotFoundException.class);
    }

    @Test
    void create_debeLanzarInterventionNotActive_cuandoEstadoNoEsPlannedNiInProgress() {
        Long interventionId = 7L;
        LocalDate startDate = LocalDate.of(2026, 9, 1);
        Intervention intervention = intervention(InterventionStatus.COMPLETED, startDate);

        when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(intervention));

        assertThatThrownBy(() -> followUpService.create(interventionId, validRequest(startDate.plusDays(1))))
                .isInstanceOf(InterventionNotActiveException.class);
    }

    @Test
    void create_debeLanzarInvalidFollowUpDate_cuandoFechaEsAnteriorAStartDate() {
        Long interventionId = 8L;
        LocalDate startDate = LocalDate.of(2026, 9, 10);
        Intervention intervention = intervention(InterventionStatus.IN_PROGRESS, startDate);

        when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(intervention));

        assertThatThrownBy(() -> followUpService.create(interventionId, validRequest(startDate.minusDays(1))))
                .isInstanceOf(InvalidFollowUpDateException.class);
    }

    @Test
    void listByIntervention_debeRetornarListado_cuandoIntervencionTieneFollowUps() {
        Long interventionId = 5L;
        Intervention intervention = intervention(InterventionStatus.PLANNED, LocalDate.now());
        FollowUp followUp = new FollowUp(interventionId, LocalDate.now(), "Observación", FollowUpResult.PENDING);

        when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(followUpRepository.findByInterventionIdAndDeletedFalseOrderByFollowUpDateAsc(interventionId))
                .thenReturn(List.of(followUp));

        List<FollowUpResponse> responses = followUpService.listByIntervention(interventionId);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).interventionId()).isEqualTo(interventionId);
    }

    @Test
    void listByIntervention_debeRetornarListaVacia_cuandoIntervencionNoTieneFollowUps() {
        Long interventionId = 5L;
        Intervention intervention = intervention(InterventionStatus.PLANNED, LocalDate.now());

        when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(followUpRepository.findByInterventionIdAndDeletedFalseOrderByFollowUpDateAsc(interventionId))
                .thenReturn(List.of());

        List<FollowUpResponse> responses = followUpService.listByIntervention(interventionId);

        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();
    }

    @Test
    void listByIntervention_debeLanzarInterventionNotFound_cuandoNoExisteIntervencion() {
        Long interventionId = 999L;
        when(interventionRepository.findById(interventionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followUpService.listByIntervention(interventionId))
                .isInstanceOf(InterventionNotFoundException.class);
    }
}
