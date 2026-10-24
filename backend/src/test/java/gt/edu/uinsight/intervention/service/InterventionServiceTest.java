// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.intervention.service;

import gt.edu.uinsight.intervention.dto.request.CreateInterventionRequest;
import gt.edu.uinsight.intervention.dto.request.UpdateInterventionStatusRequest;
import gt.edu.uinsight.intervention.dto.response.InterventionResponse;
import gt.edu.uinsight.intervention.entity.Intervention;
import gt.edu.uinsight.intervention.entity.InterventionStatus;
import gt.edu.uinsight.intervention.entity.InterventionType;
import gt.edu.uinsight.intervention.exception.AlertNotActiveException;
import gt.edu.uinsight.intervention.exception.AlertNotFoundException;
import gt.edu.uinsight.intervention.exception.InterventionNotFoundException;
import gt.edu.uinsight.intervention.exception.InvalidStatusTransitionException;
import gt.edu.uinsight.intervention.repository.InterventionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InterventionServiceTest {

    @Mock
    private InterventionRepository interventionRepository;

    @Mock
    private AlertValidationPort alertValidationPort;

    @InjectMocks
    private InterventionService interventionService;

    private CreateInterventionRequest validRequest() {
        return new CreateInterventionRequest(
                InterventionType.TUTORING,
                "Tutoría de refuerzo en matemática",
                "Lic. Ana Pérez",
                LocalDate.now()
        );
    }

    @Test
    void create_debeRegistrarIntervencion_cuandoAlertaExisteYEstaActiva() {
        Long alertId = 10L;
        when(alertValidationPort.exists(alertId)).thenReturn(true);
        when(alertValidationPort.isActive(alertId)).thenReturn(true);
        when(interventionRepository.save(any(Intervention.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InterventionResponse response = interventionService.create(alertId, validRequest());

        assertThat(response.alertId()).isEqualTo(alertId);
        assertThat(response.type()).isEqualTo(InterventionType.TUTORING);
        assertThat(response.status()).isEqualTo(gt.edu.uinsight.intervention.entity.InterventionStatus.PLANNED);
    }

    @Test
    void create_debeLanzarAlertNotFound_cuandoLaAlertaNoExiste() {
        Long alertId = 999L;
        when(alertValidationPort.exists(alertId)).thenReturn(false);

        assertThatThrownBy(() -> interventionService.create(alertId, validRequest()))
                .isInstanceOf(AlertNotFoundException.class);
    }

    @Test
    void create_debeLanzarAlertNotActive_cuandoLaAlertaEstaResuelta() {
        Long alertId = 11L;
        when(alertValidationPort.exists(alertId)).thenReturn(true);
        when(alertValidationPort.isActive(alertId)).thenReturn(false);

        assertThatThrownBy(() -> interventionService.create(alertId, validRequest()))
                .isInstanceOf(AlertNotActiveException.class);
    }

    private Intervention interventionWithStatus(Long id, InterventionStatus status) {
        Intervention intervention = new Intervention(10L, InterventionType.TUTORING,
                "Tutoría de refuerzo", "Lic. Ana Pérez", LocalDate.now());
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

    @Test
    void updateStatus_debeLanzarNotFound_cuandoLaIntervencionNoExiste() {
        when(interventionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interventionService.updateStatus(99L,
                new UpdateInterventionStatusRequest(InterventionStatus.IN_PROGRESS)))
                .isInstanceOf(InterventionNotFoundException.class);
    }

    @Test
    void updateStatus_debePermitir_dePlannedAInProgress() {
        Intervention intervention = interventionWithStatus(1L, InterventionStatus.PLANNED);
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));
        when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> inv.getArgument(0));

        InterventionResponse response = interventionService.updateStatus(1L,
                new UpdateInterventionStatusRequest(InterventionStatus.IN_PROGRESS));

        assertThat(response.status()).isEqualTo(InterventionStatus.IN_PROGRESS);
    }

    @Test
    void updateStatus_debePermitir_deInProgressACompleted() {
        Intervention intervention = interventionWithStatus(1L, InterventionStatus.IN_PROGRESS);
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));
        when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> inv.getArgument(0));

        InterventionResponse response = interventionService.updateStatus(1L,
                new UpdateInterventionStatusRequest(InterventionStatus.COMPLETED));

        assertThat(response.status()).isEqualTo(InterventionStatus.COMPLETED);
    }

    @Test
    void updateStatus_debePermitirCancelled_desdePlanned() {
        Intervention intervention = interventionWithStatus(1L, InterventionStatus.PLANNED);
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));
        when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> inv.getArgument(0));

        InterventionResponse response = interventionService.updateStatus(1L,
                new UpdateInterventionStatusRequest(InterventionStatus.CANCELLED));

        assertThat(response.status()).isEqualTo(InterventionStatus.CANCELLED);
    }

    @Test
    void updateStatus_debeRechazarSalto_dePlannedACompletedDirectamente() {
        Intervention intervention = interventionWithStatus(1L, InterventionStatus.PLANNED);
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));

        assertThatThrownBy(() -> interventionService.updateStatus(1L,
                new UpdateInterventionStatusRequest(InterventionStatus.COMPLETED)))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @ParameterizedTest
    @EnumSource(value = InterventionStatus.class, names = {"COMPLETED", "CANCELLED"})
    void updateStatus_debeRechazarCualquierTransicion_desdeUnEstadoTerminal(InterventionStatus terminalStatus) {
        Intervention intervention = interventionWithStatus(1L, terminalStatus);
        when(interventionRepository.findById(1L)).thenReturn(Optional.of(intervention));

        assertThatThrownBy(() -> interventionService.updateStatus(1L,
                new UpdateInterventionStatusRequest(InterventionStatus.IN_PROGRESS)))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }
}
