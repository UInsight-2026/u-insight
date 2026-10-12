package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.analytics.summary.entity.SectionSummary;
import gt.edu.uinsight.alert.engine.RiskEngine;
import gt.edu.uinsight.alert.engine.RiskResult;
import gt.edu.uinsight.report.dto.response.AnalyticsSnapshot;
import gt.edu.uinsight.report.dto.response.RiskSnapshot;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class B7RiskGatewayTest {

    private static final Long SECCION = 10L;

    /** Analitica completa de B6: media baja y tendencia negativa. */
    private static AnalyticsSnapshot analiticaCompleta() {
        return new AnalyticsSnapshot(SECCION, 55.0, 54.0, 30, 3.0, "NEGATIVE", -2.5, true);
    }

    private static B7RiskGateway gatewayCon(AnalyticsGateway analytics, RiskEngine motor) {
        return new B7RiskGateway(analytics, motor);
    }

    private static RiskResult resultadoDelMotor(String nivel) {
        return new RiskResult(nivel, 55.5, 5, 3, 1, List.of("MEDIA_BAJA", "TENDENCIA_NEGATIVA"));
    }

    @Test
    void deberiaDevolverElNivelDeRiesgoQueCalculaElMotor() {
        RiskEngine motor = mock(RiskEngine.class);
        when(motor.evaluate(any())).thenReturn(resultadoDelMotor("MEDIUM"));

        RiskSnapshot riesgo = gatewayCon(seccion -> analiticaCompleta(), motor)
                .getSectionRisk(SECCION);

        assertTrue(riesgo.isAvailable());
        assertEquals("MEDIUM", riesgo.getRiskLevel());
        assertEquals("B7_PARTIAL", riesgo.getRiskSource());
    }

    @Test
void deberiaEnviarLosIndicadoresDisponiblesAlMotor() {
    RiskEngine motor = mock(RiskEngine.class);
    when(motor.evaluate(any())).thenReturn(resultadoDelMotor("LOW"));

    gatewayCon(seccion -> analiticaCompleta(), motor)
            .getSectionRisk(SECCION);

    ArgumentCaptor<SectionSummary> entrada =
            ArgumentCaptor.forClass(SectionSummary.class);

    verify(motor).evaluate(entrada.capture());

    SectionSummary resumen = entrada.getValue();

    assertEquals(SECCION, resumen.getSectionId());
    assertEquals(55.0, resumen.getCentralTendencyData().mean());
    assertEquals(54.0, resumen.getCentralTendencyData().median());
    assertEquals(
            3.0,
            resumen.getDispersionData().standardDeviation().doubleValue()
    );
    assertNull(resumen.getPositionData());
}

    @Test
    void deberiaDevolverRiesgoNoDisponibleCuandoB6NoResponde() {
        RiskEngine motor = mock(RiskEngine.class);

        RiskSnapshot riesgo = gatewayCon(AnalyticsSnapshot::unavailable, motor)
                .getSectionRisk(SECCION);

        assertFalse(riesgo.isAvailable());
        assertNull(riesgo.getRiskLevel(), "sin analitica no se puede afirmar que el riesgo sea bajo");
        assertEquals("NONE", riesgo.getRiskSource());
        // Ni siquiera se molesta al motor si no hay con que alimentarlo.
        verify(motor, never()).evaluate(any());
    }

    @Test
    void deberiaDegradarSiElMotorFalla() {
        RiskEngine motor = mock(RiskEngine.class);
        when(motor.evaluate(any())).thenThrow(new IllegalStateException("motor caido"));

        RiskSnapshot riesgo = gatewayCon(seccion -> analiticaCompleta(), motor)
                .getSectionRisk(SECCION);

        assertFalse(riesgo.isAvailable());
        assertNull(riesgo.getRiskLevel());
        assertEquals("NONE", riesgo.getRiskSource());
    }

    @Test
void deberiaTolerarQueB6EntregueLaAnaliticaIncompleta() {
    AnalyticsSnapshot soloMedia =
            new AnalyticsSnapshot(
                    SECCION, 48.0, null, 12,
                    null, null, null, true
            );

    RiskEngine motor = mock(RiskEngine.class);
    when(motor.evaluate(any())).thenReturn(resultadoDelMotor("HIGH"));

    RiskSnapshot riesgo =
            gatewayCon(seccion -> soloMedia, motor)
                    .getSectionRisk(SECCION);

    assertEquals("HIGH", riesgo.getRiskLevel());

    ArgumentCaptor<SectionSummary> entrada =
            ArgumentCaptor.forClass(SectionSummary.class);

    verify(motor).evaluate(entrada.capture());

    SectionSummary resumen = entrada.getValue();

    assertEquals(48.0, resumen.getCentralTendencyData().mean());
    assertNull(resumen.getCentralTendencyData().median());
    assertNull(resumen.getDispersionData().standardDeviation());
}
        }
