package gt.edu.uinsight.alert.service;

import gt.edu.uinsight.alert.dto.request.UpdateAlertStatusRequest;
import gt.edu.uinsight.alert.dto.response.AlertResponse;
import gt.edu.uinsight.alert.model.Alert;
import gt.edu.uinsight.alert.model.AlertStatus;
import gt.edu.uinsight.alert.repository.AlertRepository;
import gt.edu.uinsight.common.exception.BusinessRuleException;
import gt.edu.uinsight.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository repository;

    private AlertService service;

    @BeforeEach
    void setUp() {
        service = new AlertService(repository);
    }

    private Alert alertWithStatus(Long id, AlertStatus status) {
        Alert alert = new Alert(10, "HIGH", 3, 2, 1);
        setId(alert, id);
        alert.setStatus(status);
        alert.setAlertType("RISK_EVALUATION");
        alert.setSeverity("HIGH");
        alert.setTitle("Riesgo HIGH detectado en la sección 10");
        return alert;
    }

    private void setId(Alert alert, Long id) {
        try {
            Field field = Alert.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(alert, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void findAll_debeMapearTodasLasAlertas() {
        when(repository.findAll()).thenReturn(List.of(
                alertWithStatus(1L, AlertStatus.NEW),
                alertWithStatus(2L, AlertStatus.RESOLVED)
        ));

        List<AlertResponse> result = service.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).status()).isEqualTo(AlertStatus.NEW);
    }

    @Test
    void findActive_debeExcluirResolvedYDismissed() {
        when(repository.findByStatusNotIn(anySet())).thenReturn(List.of(
                alertWithStatus(1L, AlertStatus.NEW),
                alertWithStatus(2L, AlertStatus.IN_PROGRESS)
        ));

        List<AlertResponse> result = service.findActive();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(AlertResponse::status)
                .doesNotContain(AlertStatus.RESOLVED, AlertStatus.DISMISSED);
    }

    @Test
    void findById_debeRetornarLaAlerta_cuandoExiste() {
        when(repository.findById(1012L)).thenReturn(Optional.of(alertWithStatus(1012L, AlertStatus.NEW)));

        AlertResponse response = service.findById(1012L);

        assertThat(response.id()).isEqualTo(1012L);
        assertThat(response.title()).isEqualTo("Riesgo HIGH detectado en la sección 10");
    }

    @Test
    void findById_debeLanzarNotFound_cuandoNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateStatus_debeLanzarNotFound_cuandoLaAlertaNoExiste() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(999L, new UpdateAlertStatusRequest(AlertStatus.UNDER_REVIEW)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateStatus_debePermitir_deNewAUnderReview() {
        Alert alert = alertWithStatus(1L, AlertStatus.NEW);
        when(repository.findById(1L)).thenReturn(Optional.of(alert));
        when(repository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));

        AlertResponse response = service.updateStatus(1L, new UpdateAlertStatusRequest(AlertStatus.UNDER_REVIEW));

        assertThat(response.status()).isEqualTo(AlertStatus.UNDER_REVIEW);
        assertThat(response.resolvedAt()).isNull();
    }

    @Test
    void updateStatus_debePermitir_deUnderReviewAInProgress() {
        Alert alert = alertWithStatus(1L, AlertStatus.UNDER_REVIEW);
        when(repository.findById(1L)).thenReturn(Optional.of(alert));
        when(repository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));

        AlertResponse response = service.updateStatus(1L, new UpdateAlertStatusRequest(AlertStatus.IN_PROGRESS));

        assertThat(response.status()).isEqualTo(AlertStatus.IN_PROGRESS);
    }

    @Test
    void updateStatus_debePermitir_deInProgressAResolved_yRegistrarFechaDeResolucion() {
        Alert alert = alertWithStatus(1L, AlertStatus.IN_PROGRESS);
        when(repository.findById(1L)).thenReturn(Optional.of(alert));
        when(repository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));

        AlertResponse response = service.updateStatus(1L, new UpdateAlertStatusRequest(AlertStatus.RESOLVED));

        assertThat(response.status()).isEqualTo(AlertStatus.RESOLVED);
        assertThat(response.resolvedAt()).isNotNull();
    }

    @Test
    void updateStatus_debePermitirDismissed_desdeCualquierEstadoNoTerminal() {
        Alert alert = alertWithStatus(1L, AlertStatus.NEW);
        when(repository.findById(1L)).thenReturn(Optional.of(alert));
        when(repository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));

        AlertResponse response = service.updateStatus(1L, new UpdateAlertStatusRequest(AlertStatus.DISMISSED));

        assertThat(response.status()).isEqualTo(AlertStatus.DISMISSED);
        assertThat(response.resolvedAt()).isNotNull();
    }

    @Test
    void updateStatus_debeRechazarSalto_deNewARESOLVEDDirectamente() {
        Alert alert = alertWithStatus(1L, AlertStatus.NEW);
        when(repository.findById(1L)).thenReturn(Optional.of(alert));

        assertThatThrownBy(() -> service.updateStatus(1L, new UpdateAlertStatusRequest(AlertStatus.RESOLVED)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @ParameterizedTest
    @EnumSource(value = AlertStatus.class, names = {"RESOLVED", "DISMISSED"})
    void updateStatus_debeRechazarCualquierTransicion_desdeUnEstadoTerminal(AlertStatus terminalStatus) {
        Alert alert = alertWithStatus(1L, terminalStatus);
        when(repository.findById(1L)).thenReturn(Optional.of(alert));

        assertThatThrownBy(() -> service.updateStatus(1L, new UpdateAlertStatusRequest(AlertStatus.UNDER_REVIEW)))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void updateStatus_debeRechazarRetroceso_deInProgressANew() {
        Alert alert = alertWithStatus(1L, AlertStatus.IN_PROGRESS);
        when(repository.findById(1L)).thenReturn(Optional.of(alert));

        assertThatThrownBy(() -> service.updateStatus(1L, new UpdateAlertStatusRequest(AlertStatus.NEW)))
                .isInstanceOf(BusinessRuleException.class);
    }
}
