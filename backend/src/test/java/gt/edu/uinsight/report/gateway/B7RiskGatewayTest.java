package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.alert.dto.section.SectionIndicatorsDto;
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
    void deberiaEnviarUnPercentil90NeutroParaNoDispararLaQuintaReglaDelMotor() {
        // La regla "percentil90 < 20" se dispararia con un valor ausente, y
        // ninguna seccion podria salir nunca en riesgo bajo por esa regla.
        RiskEngine motor = mock(RiskEngine.class);
        when(motor.evaluate(any())).thenReturn(resultadoDelMotor("LOW"));

        gatewayCon(seccion -> analiticaCompleta(), motor).getSectionRisk(SECCION);

        ArgumentCaptor<SectionIndicatorsDto> entrada = ArgumentCaptor.forClass(SectionIndicatorsDto.class);
        verify(motor).evaluate(entrada.capture());
        assertTrue(entrada.getValue().getPosition().getPercentile90() >= 20.0,
                "el percentil enviado no debe disparar la regla percentil90 < 20 del motor");
        assertEquals(55.0, entrada.getValue().getCentralTendency().getMean());
        assertEquals(3.0, entrada.getValue().getDispersion().getStdDev());
        assertEquals(-2.5, entrada.getValue().getTrend().getValue());
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
        // B6 captura errores por componente: puede traer media y faltarle el resto.
        AnalyticsSnapshot soloMedia =
                new AnalyticsSnapshot(SECCION, 48.0, null, 12, null, null, null, true);
        RiskEngine motor = mock(RiskEngine.class);
        when(motor.evaluate(any())).thenReturn(resultadoDelMotor("HIGH"));

        RiskSnapshot riesgo = gatewayCon(seccion -> soloMedia, motor).getSectionRisk(SECCION);

        assertEquals("HIGH", riesgo.getRiskLevel());

        ArgumentCaptor<SectionIndicatorsDto> entrada = ArgumentCaptor.forClass(SectionIndicatorsDto.class);
        verify(motor).evaluate(entrada.capture());
        assertEquals(0.0, entrada.getValue().getDispersion().getStdDev(), "los componentes ausentes van en cero");
        assertEquals(0.0, entrada.getValue().getTrend().getValue());
    }
}
