// Celula C4 - Intervenciones y Seguimiento | Equipo: Diego Flores, Javier Iboy, Luis Sanchez, Leandro Perez, Wesley Tuy
package gt.edu.uinsight.intervention.service;

import gt.edu.uinsight.intervention.dto.request.CreateInterventionRequest;
import gt.edu.uinsight.intervention.dto.response.InterventionResponse;
import gt.edu.uinsight.intervention.entity.Intervention;
import gt.edu.uinsight.intervention.entity.InterventionType;
import gt.edu.uinsight.intervention.exception.AlertNotActiveException;
import gt.edu.uinsight.intervention.exception.AlertNotFoundException;
import gt.edu.uinsight.intervention.repository.InterventionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

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
}
