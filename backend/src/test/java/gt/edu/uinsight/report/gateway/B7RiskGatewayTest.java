package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.alert.engine.RiskEngine;
import gt.edu.uinsight.alert.engine.RiskResult;
import gt.edu.uinsight.analytics.centraltendency.dto.response.CentralTendencyResponse;
import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.analytics.summary.service.SummaryService;
import gt.edu.uinsight.report.dto.response.RiskSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class B7RiskGatewayTest {

    private static final Long SECCION = 10L;

    private final SummaryService summaryService = mock(SummaryService.class);
    private final RiskEngine motor = mock(RiskEngine.class);
    private final B7RiskGateway gateway = new B7RiskGateway(summaryService, motor);

    /** Resumen de B6 con tendencia central, que es el minimo para evaluar. */
    private static SectionSummary resumenCompleto() {
        SectionSummary resumen = new SectionSummary();
        resumen.setSectionId(SECCION);
        resumen.setCentralTendencyData(new CentralTendencyResponse(30, 55.0, 54.0, List.of()));
        return resumen;
    }

    private static RiskResult salidaDeB7(String nivel) {
        return new RiskResult(nivel, 55.5, 5, 1, 1, List.of("MEDIA_BAJA"));
    }

    @Test
    @DisplayName("devuelve el nivel de riesgo que calcula B7")
    void deberiaDevolverElNivelDeRiesgoQueCalculaB7() {
        when(summaryService.getSummary(SECCION)).thenReturn(resumenCompleto());
        when(motor.evaluate(any())).thenReturn(salidaDeB7("MEDIUM"));

        RiskSnapshot riesgo = gateway.getSectionRisk(SECCION);

        assertTrue(riesgo.isAvailable());
        assertEquals("MEDIUM", riesgo.getRiskLevel());
        assertEquals("B7_PARTIAL", riesgo.getRiskSource());
    }

    @Test
    @DisplayName("acepta los tres niveles de la escala de C5")
    void deberiaAceptarLosTresNivelesDeLaEscala() {
        when(summaryService.getSummary(SECCION)).thenReturn(resumenCompleto());

        when(motor.evaluate(any())).thenReturn(salidaDeB7("LOW"));
        assertEquals("LOW", gateway.getSectionRisk(SECCION).getRiskLevel());

        when(motor.evaluate(any())).thenReturn(salidaDeB7("MEDIUM"));
        assertEquals("MEDIUM", gateway.getSectionRisk(SECCION).getRiskLevel());

        when(motor.evaluate(any())).thenReturn(salidaDeB7("HIGH"));
        assertEquals("HIGH", gateway.getSectionRisk(SECCION).getRiskLevel());
    }

    @Test
    @DisplayName("entrega al motor el resumen de B6 sin modificarlo")
    void deberiaEntregarElResumenTalCual() {
        // C5 ya no traduce indicadores: lo que B6 calcula es lo que B7 evalua.
        // Si en el camino se construyera otro objeto, los valores podrian cambiar
        // sin que nadie lo note.
        SectionSummary resumen = resumenCompleto();
        when(summaryService.getSummary(SECCION)).thenReturn(resumen);
        when(motor.evaluate(any())).thenReturn(salidaDeB7("LOW"));

        gateway.getSectionRisk(SECCION);

        ArgumentCaptor<SectionSummary> enviado = ArgumentCaptor.forClass(SectionSummary.class);
        verify(motor).evaluate(enviado.capture());
        assertSame(resumen, enviado.getValue(), "el resumen viaja intacto hasta el motor");
    }

    @Test
    @DisplayName("sin analitica de B6 no se afirma ningun nivel de riesgo")
    void deberiaDevolverRiesgoNoDisponibleSinAnalitica() {
        when(summaryService.getSummary(SECCION)).thenReturn(new SectionSummary());

        RiskSnapshot riesgo = gateway.getSectionRisk(SECCION);

        assertFalse(riesgo.isAvailable());
        assertNull(riesgo.getRiskLevel(), "sin analitica no se puede afirmar que el riesgo sea bajo");
        assertEquals("NONE", riesgo.getRiskSource());
        // Ni siquiera se molesta a B7 si no hay con que alimentarlo.
        verify(motor, never()).evaluate(any());
    }

    @Test
    @DisplayName("un resumen nulo de B6 se trata como ausencia, no como error")
    void deberiaTolerarResumenNulo() {
        when(summaryService.getSummary(SECCION)).thenReturn(null);

        RiskSnapshot riesgo = gateway.getSectionRisk(SECCION);

        assertFalse(riesgo.isAvailable());
        assertEquals("NONE", riesgo.getRiskSource());
        verify(motor, never()).evaluate(any());
    }

    @Test
    @DisplayName("un fallo de B6 no tumba el reporte")
    void deberiaDegradarSiB6Falla() {
        when(summaryService.getSummary(SECCION)).thenThrow(new IllegalStateException("B6 no responde"));

        RiskSnapshot riesgo = gateway.getSectionRisk(SECCION);

        assertFalse(riesgo.isAvailable());
        assertNull(riesgo.getRiskLevel());
        verify(motor, never()).evaluate(any());
    }

    @Test
    @DisplayName("un fallo del motor de B7 no tumba el reporte")
    void deberiaDegradarSiElMotorDeB7Falla() {
        when(summaryService.getSummary(SECCION)).thenReturn(resumenCompleto());
        when(motor.evaluate(any())).thenThrow(new IllegalStateException("motor caido"));

        RiskSnapshot riesgo = gateway.getSectionRisk(SECCION);

        assertFalse(riesgo.isAvailable());
        assertNull(riesgo.getRiskLevel());
        assertEquals("NONE", riesgo.getRiskSource());
    }

    @Test
    @DisplayName("una respuesta sin nivel se trata como no disponible")
    void deberiaTolerarRespuestaSinNivel() {
        when(summaryService.getSummary(SECCION)).thenReturn(resumenCompleto());
        when(motor.evaluate(any())).thenReturn(salidaDeB7(null));

        RiskSnapshot riesgo = gateway.getSectionRisk(SECCION);

        assertFalse(riesgo.isAvailable());
        assertEquals("NONE", riesgo.getRiskSource());
    }

    @Test
    @DisplayName("un nivel fuera de la escala de C5 se descarta")
    void deberiaTratarUnNivelDesconocidoComoNoDisponible() {
        when(summaryService.getSummary(SECCION)).thenReturn(resumenCompleto());
        when(motor.evaluate(any())).thenReturn(salidaDeB7("CRITICO"));

        RiskSnapshot riesgo = gateway.getSectionRisk(SECCION);

        assertFalse(riesgo.isAvailable());
        assertNull(riesgo.getRiskLevel());
    }
}
