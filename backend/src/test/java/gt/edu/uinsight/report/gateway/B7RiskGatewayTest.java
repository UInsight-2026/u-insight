package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.alert.b7.model.RiskInput;
import gt.edu.uinsight.alert.b7.model.RiskOutput;
import gt.edu.uinsight.alert.b7.Service.RiskEngineService;
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
        return new AnalyticsSnapshot(SECCION, 55.0, 54.0, 30, 3.0, "NEGATIVE", -2.5, "B3_FIXED", true);
    }

    private static B7RiskGateway gatewayCon(AnalyticsGateway analytics, RiskEngineService motor) {
        return new B7RiskGateway(analytics, motor);
    }

    private static RiskOutput salidaDeB7(String nivel) {
        return new RiskOutput(nivel, "Revision recomendada", 55.5, 3, 1, 1, List.of());
    }

    @Test
    void deberiaDevolverElNivelDeRiesgoQueCalculaB7() {
        RiskEngineService motor = mock(RiskEngineService.class);
        when(motor.evaluarRiesgo(any())).thenReturn(salidaDeB7("MODERADO"));

        RiskSnapshot riesgo = gatewayCon(seccion -> analiticaCompleta(), motor)
                .getSectionRisk(SECCION);

        assertTrue(riesgo.isAvailable());
        assertEquals("MEDIUM", riesgo.getRiskLevel());
        assertEquals("B7_PARTIAL", riesgo.getRiskSource());
    }

    @Test
    void deberiaTraducirLosTresNivelesDeB7AlaEscalaDeC5() {
        RiskEngineService motor = mock(RiskEngineService.class);
        B7RiskGateway gateway = gatewayCon(seccion -> analiticaCompleta(), motor);

        when(motor.evaluarRiesgo(any())).thenReturn(salidaDeB7("BAJO"));
        assertEquals("LOW", gateway.getSectionRisk(SECCION).getRiskLevel());

        when(motor.evaluarRiesgo(any())).thenReturn(salidaDeB7("MODERADO"));
        assertEquals("MEDIUM", gateway.getSectionRisk(SECCION).getRiskLevel());

        when(motor.evaluarRiesgo(any())).thenReturn(salidaDeB7("ALTO"));
        assertEquals("HIGH", gateway.getSectionRisk(SECCION).getRiskLevel());
    }

    @Test
    void deberiaEnviarUnPercentil90NeutroParaNoDispararLaTerceraReglaDeB7() {
        // La regla "percentil90 < 20" de B7 se dispararia con el 0.0 por omision
        // del double primitivo, y ninguna seccion podria salir nunca en riesgo bajo.
        RiskEngineService motor = mock(RiskEngineService.class);
        when(motor.evaluarRiesgo(any())).thenReturn(salidaDeB7("BAJO"));

        gatewayCon(seccion -> analiticaCompleta(), motor).getSectionRisk(SECCION);

        ArgumentCaptor<RiskInput> entrada = ArgumentCaptor.forClass(RiskInput.class);
        verify(motor).evaluarRiesgo(entrada.capture());
        assertTrue(entrada.getValue().getPercentil90() >= 20.0,
                "el percentil enviado no debe disparar la regla percentil90 < 20 de B7");
        assertEquals(55.0, entrada.getValue().getMedia());
        assertEquals(3.0, entrada.getValue().getDesviacion());
        assertEquals(-2.5, entrada.getValue().getTendencia());
    }

    @Test
    void deberiaDevolverRiesgoNoDisponibleCuandoB6NoResponde() {
        RiskEngineService motor = mock(RiskEngineService.class);

        RiskSnapshot riesgo = gatewayCon(AnalyticsSnapshot::unavailable, motor)
                .getSectionRisk(SECCION);

        assertFalse(riesgo.isAvailable());
        assertNull(riesgo.getRiskLevel(), "sin analitica no se puede afirmar que el riesgo sea bajo");
        assertEquals("NONE", riesgo.getRiskSource());
        // Ni siquiera se molesta a B7 si no hay con que alimentarlo.
        verify(motor, never()).evaluarRiesgo(any());
    }

    @Test
    void deberiaDegradarSiElMotorDeB7Falla() {
        RiskEngineService motor = mock(RiskEngineService.class);
        when(motor.evaluarRiesgo(any())).thenThrow(new IllegalStateException("motor caido"));

        RiskSnapshot riesgo = gatewayCon(seccion -> analiticaCompleta(), motor)
                .getSectionRisk(SECCION);

        assertFalse(riesgo.isAvailable());
        assertNull(riesgo.getRiskLevel());
        assertEquals("NONE", riesgo.getRiskSource());
    }

    @Test
    void deberiaTratarUnNivelDesconocidoDeB7ComoNoDisponible() {
        RiskEngineService motor = mock(RiskEngineService.class);
        when(motor.evaluarRiesgo(any())).thenReturn(salidaDeB7("CRITICO"));

        RiskSnapshot riesgo = gatewayCon(seccion -> analiticaCompleta(), motor)
                .getSectionRisk(SECCION);

        assertFalse(riesgo.isAvailable());
        assertNull(riesgo.getRiskLevel());
    }

    @Test
    void deberiaTolerarQueB6EntregueLaAnaliticaIncompleta() {
        // B6 captura errores por componente: puede traer media y faltarle el resto.
        AnalyticsSnapshot soloMedia =
                new AnalyticsSnapshot(SECCION, 48.0, null, 12, null, null, null,
                        AnalyticsSnapshot.DISPERSION_NO_DISPONIBLE, true);
        RiskEngineService motor = mock(RiskEngineService.class);
        when(motor.evaluarRiesgo(any())).thenReturn(salidaDeB7("ALTO"));

        RiskSnapshot riesgo = gatewayCon(seccion -> soloMedia, motor).getSectionRisk(SECCION);

        assertEquals("HIGH", riesgo.getRiskLevel());

        ArgumentCaptor<RiskInput> entrada = ArgumentCaptor.forClass(RiskInput.class);
        verify(motor).evaluarRiesgo(entrada.capture());
        assertEquals(0.0, entrada.getValue().getDesviacion(), "los componentes ausentes van en cero");
        assertEquals(0.0, entrada.getValue().getTendencia());
    }
}
