package gt.edu.uinsight.alertrule.service;

import gt.edu.uinsight.alertrule.dto.request.CreateAlertRuleRequest;
import gt.edu.uinsight.alertrule.dto.request.UpdateStatusRequest;
import gt.edu.uinsight.alertrule.dto.response.AlertRuleResponse;
import gt.edu.uinsight.alertrule.entity.AlertRule;
import gt.edu.uinsight.alertrule.repository.AlertRuleRepository;
import gt.edu.uinsight.common.exception.ResourceNotFoundException;
import gt.edu.uinsight.integration.c1.C1ConfigurationClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertRuleServiceTest {

    @Mock
    private AlertRuleRepository repository;

    @Mock
    private C1ConfigurationClient c1Client;

    private AlertRuleService service;

    @BeforeEach
    void setUp() {
        service = new AlertRuleService(repository, c1Client);
    }

    private CreateAlertRuleRequest validRequest() {
        CreateAlertRuleRequest request = new CreateAlertRuleRequest();
        request.setName("Promedio bajo con tendencia negativa");
        request.setDescription("Dispara cuando el promedio cae y la tendencia es negativa");
        request.setConditionExpression("media<61&&tendencia==NEGATIVE");
        request.setSeverity("HIGH");
        return request;
    }

    private AlertRule savedRule(Long id) {
        AlertRule rule = new AlertRule();
        rule.setId(id);
        rule.setName("Promedio bajo con tendencia negativa");
        rule.setDescription("Dispara cuando el promedio cae y la tendencia es negativa");
        rule.setConditionExpression("media<61&&tendencia==NEGATIVE");
        rule.setSeverity("HIGH");
        rule.setActive(true);
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());
        return rule;
    }

    @Test
    void createRule_debeGuardarRegla_cuandoNombreNoExiste() {
        when(repository.existsByNameIgnoreCase(any())).thenReturn(false);
        when(repository.save(any(AlertRule.class))).thenAnswer(inv -> {
            AlertRule rule = inv.getArgument(0);
            rule.setId(1L);
            return rule;
        });

        AlertRuleResponse response = service.createRule(validRequest());

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getSeverity()).isEqualTo("HIGH");
        assertThat(response.getActive()).isTrue();
    }

    @Test
    void createRule_debeRechazar_cuandoYaExisteUnaReglaConElMismoNombre() {
        when(repository.existsByNameIgnoreCase("Promedio bajo con tendencia negativa")).thenReturn(true);

        assertThatThrownBy(() -> service.createRule(validRequest()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe una regla");

        verify(repository, never()).save(any(AlertRule.class));
    }

    @Test
    void createRule_debeRechazar_cuandoLaSeveridadNoEsValida() {
        CreateAlertRuleRequest request = validRequest();
        request.setSeverity("CRITICAL");
        when(repository.existsByNameIgnoreCase(any())).thenReturn(false);

        assertThatThrownBy(() -> service.createRule(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Severidad inválida");

        verify(repository, never()).save(any(AlertRule.class));
    }

    @Test
    void createRule_debeRechazar_cuandoLaCondicionEstaVacia() {
        CreateAlertRuleRequest request = validRequest();
        request.setConditionExpression("   ");
        when(repository.existsByNameIgnoreCase(any())).thenReturn(false);

        assertThatThrownBy(() -> service.createRule(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("expresión condicional");
    }

    @Test
    void getAllRules_debeFiltrarPorActivas_cuandoSeIndicaElParametro() {
        when(repository.findByActive(true)).thenReturn(List.of(savedRule(1L)));

        List<AlertRuleResponse> result = service.getAllRules(true);

        assertThat(result).hasSize(1);
        verify(repository).findByActive(true);
    }

    @Test
    void getAllRules_debeRetornarTodas_cuandoNoSeIndicaFiltro() {
        when(repository.findAll()).thenReturn(List.of(savedRule(1L), savedRule(2L)));

        List<AlertRuleResponse> result = service.getAllRules(null);

        assertThat(result).hasSize(2);
    }

    @Test
    void getActiveRules_debeDelegarEnFindByActiveTrue() {
        when(repository.findByActiveTrue()).thenReturn(List.of(savedRule(1L)));

        List<AlertRule> result = service.getActiveRules();

        assertThat(result).hasSize(1);
        verify(repository).findByActiveTrue();
    }

    @Test
    void getRuleById_debeLanzarNotFound_cuandoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getRuleById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateRule_debeActualizarCampos_cuandoLaReglaExiste() {
        AlertRule existing = savedRule(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.existsByNameIgnoreCaseAndIdNot(any(), eq(1L))).thenReturn(false);
        when(repository.save(any(AlertRule.class))).thenAnswer(inv -> inv.getArgument(0));

        CreateAlertRuleRequest request = validRequest();
        request.setSeverity("MEDIUM");

        AlertRuleResponse response = service.updateRule(1L, request);

        assertThat(response.getSeverity()).isEqualTo("MEDIUM");
    }

    @Test
    void updateStatus_debeDesactivarLaRegla_cuandoActiveEsFalse() {
        AlertRule existing = savedRule(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(AlertRule.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setActive(false);

        AlertRuleResponse response = service.updateStatus(1L, request);

        assertThat(response.getActive()).isFalse();
    }

    @Test
    void getSystemThresholdFromC1_debeDelegarEnElClienteDeC1() {
        when(c1Client.getLowPerformanceThreshold()).thenReturn(61.0);

        Double threshold = service.getSystemThresholdFromC1();

        assertThat(threshold).isEqualTo(61.0);
    }
}
