// Celula A5 - Gestión de evaluaciones
package gt.edu.uinsight.evaluation.service;

import gt.edu.uinsight.evaluation.api.EvaluationSummary;
import gt.edu.uinsight.evaluation.dto.request.ChangeEvaluationStatusRequest;
import gt.edu.uinsight.evaluation.dto.request.CreateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.request.UpdateEvaluationRequest;
import gt.edu.uinsight.evaluation.dto.response.EvaluationResponse;
import gt.edu.uinsight.evaluation.entity.Evaluation;
import gt.edu.uinsight.evaluation.exception.EvaluationNotEditableException;
import gt.edu.uinsight.evaluation.exception.EvaluationNotFoundException;
import gt.edu.uinsight.evaluation.exception.InvalidStatusTransitionException;
import gt.edu.uinsight.evaluation.exception.SectionNotActiveException;
import gt.edu.uinsight.evaluation.exception.SectionNotFoundException;
import gt.edu.uinsight.evaluation.exception.WeightLimitExceededException;
import gt.edu.uinsight.evaluation.mapper.EvaluationMapper;
import gt.edu.uinsight.evaluation.repository.EvaluationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias de las reglas de negocio RN1-RN6 e integración con A6 (célula A5). */
@ExtendWith(MockitoExtension.class)
class EvaluationServiceImplTest {

    private static final Long SECTION_ID = 1L;

    @Mock private EvaluationRepository repository;
    @Mock private SectionValidationPort sectionPort;
    @Mock private GradeEvaluationSyncPort gradeSyncPort;

    private EvaluationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EvaluationServiceImpl(repository, new EvaluationMapper(), sectionPort, gradeSyncPort);
    }

    // ---------------------------------------------------------------- helpers

    private static Evaluation evaluation(Long id, String status, String weight) {
        Evaluation e = new Evaluation(SECTION_ID, "Eval " + id, "EXAM", LocalDate.of(2026, 10, 15),
                new BigDecimal("100"), new BigDecimal(weight), status);
        e.setId(id);
        return e;
    }

    private static CreateEvaluationRequest createRequest(String weight) {
        return new CreateEvaluationRequest(SECTION_ID, "  Parcial 1  ", "exam", LocalDate.of(2026, 10, 15),
                new BigDecimal("100"), new BigDecimal(weight));
    }

    private static UpdateEvaluationRequest updateRequest(String weight) {
        return new UpdateEvaluationRequest("Parcial 1 v2", LocalDate.of(2026, 10, 20),
                new BigDecimal("80"), new BigDecimal(weight));
    }

    private void sectionActive() {
        when(sectionPort.exists(SECTION_ID)).thenReturn(true);
        when(sectionPort.isActive(SECTION_ID)).thenReturn(true);
    }

    private void saveReturnsArgument() {
        when(repository.save(any(Evaluation.class))).thenAnswer(inv -> {
            Evaluation e = inv.getArgument(0);
            if (e.getId() == null) {
                e.setId(99L);
            }
            return e;
        });
    }

    // ---------------------------------------------------------------- HU1 crear

    @Test
    @DisplayName("HU1: crea la evaluación en DRAFT, normaliza tipo y nombre")
    void create_ok_startsInDraft() {
        sectionActive();
        when(repository.findBySectionId(SECTION_ID)).thenReturn(List.of(evaluation(1L, "ACTIVE", "40")));
        saveReturnsArgument();

        EvaluationResponse response = service.createEvaluation(createRequest("30"));

        assertThat(response.getId()).isEqualTo(99L);
        assertThat(response.getStatus()).isEqualTo("DRAFT");
        assertThat(response.getType()).isEqualTo("EXAM");
        assertThat(response.getName()).isEqualTo("Parcial 1");
    }

    @Test
    @DisplayName("RN1: rechaza crear si la sección no existe")
    void create_sectionNotFound() {
        when(sectionPort.exists(SECTION_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.createEvaluation(createRequest("30")))
                .isInstanceOf(SectionNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("RN1: rechaza crear si la sección no está activa")
    void create_sectionNotActive() {
        when(sectionPort.exists(SECTION_ID)).thenReturn(true);
        when(sectionPort.isActive(SECTION_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.createEvaluation(createRequest("30")))
                .isInstanceOf(SectionNotActiveException.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("RN4: rechaza crear si la suma de ponderaciones supera 100")
    void create_weightLimitExceeded() {
        sectionActive();
        when(repository.findBySectionId(SECTION_ID)).thenReturn(List.of(
                evaluation(1L, "ACTIVE", "50"), evaluation(2L, "DRAFT", "30")));

        assertThatThrownBy(() -> service.createEvaluation(createRequest("25")))
                .isInstanceOf(WeightLimitExceededException.class)
                .hasMessageContaining("105");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("RN4: las evaluaciones CANCELLED no cuentan en la suma")
    void create_cancelledWeightsAreIgnored() {
        sectionActive();
        when(repository.findBySectionId(SECTION_ID)).thenReturn(List.of(evaluation(1L, "CANCELLED", "90")));
        saveReturnsArgument();

        EvaluationResponse response = service.createEvaluation(createRequest("100"));

        assertThat(response.getStatus()).isEqualTo("DRAFT");
    }

    // ---------------------------------------------------------------- HU3 actualizar

    @Test
    @DisplayName("RN4: al actualizar no cuenta dos veces su propia ponderación")
    void update_excludesOwnWeight() {
        Evaluation own = evaluation(5L, "DRAFT", "40");
        when(repository.findById(5L)).thenReturn(Optional.of(own));
        sectionActive();
        when(repository.findBySectionId(SECTION_ID)).thenReturn(List.of(own, evaluation(6L, "ACTIVE", "50")));
        saveReturnsArgument();

        EvaluationResponse response = service.updateEvaluation(5L, updateRequest("50"));

        assertThat(response.getWeight()).isEqualByComparingTo("50");
        assertThat(response.getMaximumScore()).isEqualByComparingTo("80");
        verify(gradeSyncPort, never()).publish(any());
    }

    @Test
    @DisplayName("RN5: no permite modificar una evaluación CLOSED")
    void update_closed_notEditable() {
        when(repository.findById(5L)).thenReturn(Optional.of(evaluation(5L, "CLOSED", "40")));

        assertThatThrownBy(() -> service.updateEvaluation(5L, updateRequest("40")))
                .isInstanceOf(EvaluationNotEditableException.class)
                .hasMessageContaining("CLOSED");
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("HU3: no permite modificar una evaluación CANCELLED")
    void update_cancelled_notEditable() {
        when(repository.findById(5L)).thenReturn(Optional.of(evaluation(5L, "CANCELLED", "40")));

        assertThatThrownBy(() -> service.updateEvaluation(5L, updateRequest("40")))
                .isInstanceOf(EvaluationNotEditableException.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Integración A6: al actualizar una evaluación ACTIVE se publica la nueva nota máxima")
    void update_active_publishesToGrades() {
        Evaluation own = evaluation(5L, "ACTIVE", "40");
        when(repository.findById(5L)).thenReturn(Optional.of(own));
        sectionActive();
        when(repository.findBySectionId(SECTION_ID)).thenReturn(List.of(own));
        saveReturnsArgument();

        service.updateEvaluation(5L, updateRequest("40"));

        ArgumentCaptor<EvaluationSummary> captor = ArgumentCaptor.forClass(EvaluationSummary.class);
        verify(gradeSyncPort).publish(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(5L);
        assertThat(captor.getValue().maximumScore()).isEqualByComparingTo("80");
    }

    // ---------------------------------------------------------------- HU4 cambiar estado

    @Test
    @DisplayName("RN6: DRAFT -> ACTIVE es válido y publica la evaluación a A6")
    void changeStatus_draftToActive_publishes() {
        when(repository.findById(5L)).thenReturn(Optional.of(evaluation(5L, "DRAFT", "40")));
        sectionActive();
        saveReturnsArgument();

        EvaluationResponse response = service.changeStatus(5L, new ChangeEvaluationStatusRequest("active"));

        assertThat(response.getStatus()).isEqualTo("ACTIVE");
        verify(gradeSyncPort).publish(any(EvaluationSummary.class));
    }

    @Test
    @DisplayName("RN6: ACTIVE -> CLOSED es válido y no vuelve a publicar")
    void changeStatus_activeToClosed() {
        when(repository.findById(5L)).thenReturn(Optional.of(evaluation(5L, "ACTIVE", "40")));
        saveReturnsArgument();

        EvaluationResponse response = service.changeStatus(5L, new ChangeEvaluationStatusRequest("CLOSED"));

        assertThat(response.getStatus()).isEqualTo("CLOSED");
        verify(gradeSyncPort, never()).publish(any());
    }

    @Test
    @DisplayName("RN6: CLOSED -> ACTIVE no está permitido")
    void changeStatus_closedToActive_invalid() {
        when(repository.findById(5L)).thenReturn(Optional.of(evaluation(5L, "CLOSED", "40")));

        assertThatThrownBy(() -> service.changeStatus(5L, new ChangeEvaluationStatusRequest("ACTIVE")))
                .isInstanceOf(InvalidStatusTransitionException.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("RN6: no se permiten transiciones inversas (ACTIVE -> DRAFT)")
    void changeStatus_activeToDraft_invalid() {
        when(repository.findById(5L)).thenReturn(Optional.of(evaluation(5L, "ACTIVE", "40")));

        assertThatThrownBy(() -> service.changeStatus(5L, new ChangeEvaluationStatusRequest("DRAFT")))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    @DisplayName("RN1: no se puede activar si la sección ya no está activa")
    void changeStatus_activateWithInactiveSection() {
        when(repository.findById(5L)).thenReturn(Optional.of(evaluation(5L, "DRAFT", "40")));
        when(sectionPort.exists(SECTION_ID)).thenReturn(true);
        when(sectionPort.isActive(SECTION_ID)).thenReturn(false);

        assertThatThrownBy(() -> service.changeStatus(5L, new ChangeEvaluationStatusRequest("ACTIVE")))
                .isInstanceOf(SectionNotActiveException.class);
        verify(gradeSyncPort, never()).publish(any());
    }

    // ---------------------------------------------------------------- consultas

    @Test
    @DisplayName("Consultar una evaluación inexistente lanza EvaluationNotFound (404)")
    void getById_notFound() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getEvaluationById(404L))
                .isInstanceOf(EvaluationNotFoundException.class);
    }

    @Test
    @DisplayName("HU2: sección existente sin evaluaciones devuelve lista vacía")
    void getBySection_emptyList() {
        when(sectionPort.exists(SECTION_ID)).thenReturn(true);
        when(repository.findBySectionId(SECTION_ID)).thenReturn(List.of());

        assertThat(service.getEvaluationsBySectionId(SECTION_ID)).isEmpty();
    }

    @Test
    @DisplayName("HU2: sección inexistente lanza SectionNotFound (404)")
    void getBySection_sectionNotFound() {
        when(sectionPort.exists(77L)).thenReturn(false);

        assertThatThrownBy(() -> service.getEvaluationsBySectionId(77L))
                .isInstanceOf(SectionNotFoundException.class);
    }

    @Test
    @DisplayName("API para A6: solo las evaluaciones ACTIVE son calificables")
    void isGradable_onlyActive() {
        when(repository.findById(1L)).thenReturn(Optional.of(evaluation(1L, "ACTIVE", "40")));
        when(repository.findById(2L)).thenReturn(Optional.of(evaluation(2L, "CLOSED", "40")));
        when(repository.findById(3L)).thenReturn(Optional.empty());

        assertThat(service.isGradable(1L)).isTrue();
        assertThat(service.isGradable(2L)).isFalse();
        assertThat(service.isGradable(3L)).isFalse();
    }
}
