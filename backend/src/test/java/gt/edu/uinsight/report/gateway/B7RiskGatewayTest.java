package gt.edu.uinsight.report.gateway;

import gt.edu.uinsight.alert.dto.section.SectionIndicatorsDto;
import gt.edu.uinsight.alert.engine.RiskEngine;
import gt.edu.uinsight.alert.engine.RiskResult;
import gt.edu.uinsight.report.dto.response.AnalyticsSnapshot;
import gt.edu.uinsight.report.dto.response.RiskSnapshot;
import org.junit.jupiter.api.DisplayName;
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
        return new AnalyticsSnapshot(SECCION, 55.0, 54.0, 30, 3.0, "NEGATIVE", -2.5, "B3", true);
    }

    private static B7RiskGateway gatewayCon(AnalyticsGateway analytics, RiskEngine motor) {
        return new B7RiskGateway(analytics, motor);
    }

    private static RiskResult salidaDeB7(String nivel) {
        return new RiskResult(nivel, 55.5, 5, 1, 1, List.of("MEDIA_BAJA"));
    }

    private static SectionIndicatorsDto capturarEntrada(RiskEngine motor) {
        ArgumentCaptor<SectionIndicatorsDto> entrada =
                ArgumentCaptor.forClass(SectionIndicatorsDto.class);
        verify(motor).evaluate(entrada.capture());
        return entrada.getValue();
    }

    @Test
    @DisplayName("devuelve el nivel de riesgo que calcula B7")
    void deberiaDevolverElNivelDeRiesgoQueCalculaB7() {
        RiskEngine motor = mock(RiskEngine.class);
        when(motor.evaluate(any())).thenReturn(salidaDeB7("MEDIUM"));

        RiskSnapshot riesgo = gatewayCon(seccion -> analiticaCompleta(), motor)
                .getSectionRisk(SECCION);

        assertTrue(riesgo.isAvailable());
        assertEquals("MEDIUM", riesgo.getRiskLevel());
        assertEquals("B7_PARTIAL", riesgo.getRiskSource());
    }

    @Test
    @DisplayName("acepta los tres niveles de la escala de C5")
    void deberiaAceptarLosTresNivelesDeLaEscala() {
        RiskEngine motor = mock(RiskEngine.class);
        B7RiskGateway gateway = gatewayCon(seccion -> analiticaCompleta(), motor);

        when(motor.evaluate(any())).thenReturn(salidaDeB7("LOW"));
        assertEquals("LOW", gateway.getSectionRisk(SECCION).getRiskLevel());

        when(motor.evaluate(any())).thenReturn(salidaDeB7("MEDIUM"));
        assertEquals("MEDIUM", gateway.getSectionRisk(SECCION).getRiskLevel());

        when(motor.evaluate(any())).thenReturn(salidaDeB7("HIGH"));
        assertEquals("HIGH", gateway.getSectionRisk(SECCION).getRiskLevel());
    }

    @Test
    @DisplayName("envia un percentil 90 neutro para no disparar la quinta regla de B7")
    void deberiaEnviarUnPercentil90Neutro() {
        // La regla "percentil90 < 20" de B7 se dispararia con el 0.0 por omision
        // del double primitivo, y ninguna seccion podria salir nunca en riesgo bajo.
        RiskEngine motor = mock(RiskEngine.class);
        when(motor.evaluate(any())).thenReturn(salidaDeB7("LOW"));

        gatewayCon(seccion -> analiticaCompleta(), motor).getSectionRisk(SECCION);

        SectionIndicatorsDto enviado = capturarEntrada(motor);
        assertTrue(enviado.getPosition().getPercentile90() >= 20.0,
                "el percentil enviado no debe disparar la regla percentil90 < 20 de B7");
        assertEquals(55.0, enviado.getCentralTendency().getMean());
        assertEquals(3.0, enviado.getDispersion().getStdDev());
        assertEquals(-2.5, enviado.getTrend().getValue());
    }

    @Test
    @DisplayName("una mediana ausente no se rellena con cero, que dispararia su regla")
    void deberiaRellenarLaMedianaAusenteConLaMedia() {
        // La regla de la mediana es "< 60": un cero por ausencia sumaria 15 puntos
        // de riesgo que nadie midio. Se usa la media, el otro indicador de
        // tendencia central, que si llega.
        AnalyticsSnapshot sinMediana =
                new AnalyticsSnapshot(SECCION, 72.0, null, 30, 3.0, "STABLE", 0.5, "B3", true);
        RiskEngine motor = mock(RiskEngine.class);
        when(motor.evaluate(any())).thenReturn(salidaDeB7("LOW"));

        gatewayCon(seccion -> sinMediana, motor).getSectionRisk(SECCION);

        SectionIndicatorsDto enviado = capturarEntrada(motor);
        assertEquals(72.0, enviado.getCentralTendency().getMedian(),
                "la mediana ausente toma el valor de la media, no cero");
    }

    @Test
    @DisplayName("sin analitica de B6 no se afirma ningun nivel de riesgo")
    void deberiaDevolverRiesgoNoDisponibleCuandoB6NoResponde() {
        RiskEngine motor = mock(RiskEngine.class);

        RiskSnapshot riesgo = gatewayCon(AnalyticsSnapshot::unavailable, motor)
                .getSectionRisk(SECCION);

        assertFalse(riesgo.isAvailable());
        assertNull(riesgo.getRiskLevel(), "sin analitica no se puede afirmar que el riesgo sea bajo");
        assertEquals("NONE", riesgo.getRiskSource());
        // Ni siquiera se molesta a B7 si no hay con que alimentarlo.
        verify(motor, never()).evaluate(any());
    }

    @Test
    @DisplayName("un fallo del motor de B7 no tumba el reporte")
    void deberiaDegradarSiElMotorDeB7Falla() {
        RiskEngine motor = mock(RiskEngine.class);
        when(motor.evaluate(any())).thenThrow(new IllegalStateException("motor caido"));

        RiskSnapshot riesgo = gatewayCon(seccion -> analiticaCompleta(), motor)
                .getSectionRisk(SECCION);

        assertFalse(riesgo.isAvailable());
        assertNull(riesgo.getRiskLevel());
        assertEquals("NONE", riesgo.getRiskSource());
    }

    @Test
    @DisplayName("un nivel fuera de la escala de C5 se descarta")
    void deberiaTratarUnNivelDesconocidoDeB7ComoNoDisponible() {
        RiskEngine motor = mock(RiskEngine.class);
        when(motor.evaluate(any())).thenReturn(salidaDeB7("CRITICO"));

        RiskSnapshot riesgo = gatewayCon(seccion -> analiticaCompleta(), motor)
                .getSectionRisk(SECCION);

        assertFalse(riesgo.isAvailable());
        assertNull(riesgo.getRiskLevel());
    }

    @Test
    @DisplayName("tolera que B6 entregue la analitica incompleta")
    void deberiaTolerarQueB6EntregueLaAnaliticaIncompleta() {
        // B6 captura errores por componente: puede traer media y faltarle el resto.
        AnalyticsSnapshot soloMedia =
                new AnalyticsSnapshot(SECCION, 48.0, null, 12, null, null, null,
                        AnalyticsSnapshot.DISPERSION_NO_DISPONIBLE, true);
        RiskEngine motor = mock(RiskEngine.class);
        when(motor.evaluate(any())).thenReturn(salidaDeB7("HIGH"));

        RiskSnapshot riesgo = gatewayCon(seccion -> soloMedia, motor).getSectionRisk(SECCION);

        assertEquals("HIGH", riesgo.getRiskLevel());

        SectionIndicatorsDto enviado = capturarEntrada(motor);
        assertEquals(0.0, enviado.getDispersion().getStdDev(),
                "la regla de desviacion es > 5: el cero no la dispara");
        assertEquals(0.0, enviado.getTrend().getValue(),
                "la regla de tendencia es < 0: el cero no la dispara");
    }

    @Test
    @DisplayName("los cuatro bloques van completos: el motor no comprueba nulos")
    void deberiaEnviarLosCuatroBloquesCompletos() {
        // RiskEngine lee cada indicador como double primitivo. Un bloque ausente o
        // un campo nulo lo harian fallar con NullPointerException.
        AnalyticsSnapshot soloMedia =
                new AnalyticsSnapshot(SECCION, 48.0, null, null, null, null, null,
                        AnalyticsSnapshot.DISPERSION_NO_DISPONIBLE, true);
        RiskEngine motor = mock(RiskEngine.class);
        when(motor.evaluate(any())).thenReturn(salidaDeB7("HIGH"));

        gatewayCon(seccion -> soloMedia, motor).getSectionRisk(SECCION);

        SectionIndicatorsDto enviado = capturarEntrada(motor);
        assertNotNullTodos(enviado);
    }

    private static void assertNotNullTodos(SectionIndicatorsDto dto) {
        assertTrue(dto.getCentralTendency() != null && dto.getCentralTendency().getMean() != null
                        && dto.getCentralTendency().getMedian() != null,
                "tendencia central completa");
        assertTrue(dto.getPosition() != null && dto.getPosition().getPercentile90() != null,
                "posicion completa");
        assertTrue(dto.getDispersion() != null && dto.getDispersion().getStdDev() != null,
                "dispersion completa");
        assertTrue(dto.getTrend() != null && dto.getTrend().getValue() != null,
                "tendencia completa");
    }
}
