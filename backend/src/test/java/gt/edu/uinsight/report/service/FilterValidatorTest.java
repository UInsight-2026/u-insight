//semana 3
package gt.edu.uinsight.report.service;

import gt.edu.uinsight.report.dto.filter.PageFilter;
import gt.edu.uinsight.report.dto.filter.ReportFilter;
import gt.edu.uinsight.report.exception.InvalidFilterException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilterValidatorTest {

    private final FilterValidator validator = new FilterValidator();

    private ReportFilter filtro(String period, String riskLevel, String alertStatus) {
        return new ReportFilter(period, null, null, null, riskLevel, alertStatus);
    }

    @Test
    void deberiaAceptarUnFiltroVacio() {
        assertDoesNotThrow(() -> validator.validate(filtro(null, null, null)));
        assertDoesNotThrow(() -> validator.validate(null));
    }

    @Test
    void deberiaRechazarUnRiskLevelInvalido() {
        InvalidFilterException ex = assertThrows(InvalidFilterException.class,
                () -> validator.validate(filtro(null, "ALTO", null)));
        assertTrue(ex.getMessage().contains("riskLevel"));
    }

    @Test
    void deberiaAceptarLosCincoEstadosOficialesDeAlerta() {
        for (String estado : new String[]{"NEW", "UNDER_REVIEW", "IN_PROGRESS", "RESOLVED", "DISMISSED"}) {
            assertDoesNotThrow(() -> validator.validate(filtro(null, null, estado)));
        }
    }

    @Test
    void deberiaRechazarActiveComoEstadoDeAlerta() {
        // ACTIVE se descartó: no pertenece al ciclo de vida que define C3.
        assertThrows(InvalidFilterException.class,
                () -> validator.validate(filtro(null, null, "ACTIVE")));
    }

    @Test
    void deberiaRechazarUnPeriodoConFormatoInvalido() {
        assertThrows(InvalidFilterException.class,
                () -> validator.validate(filtro("2026", null, null)));
        assertThrows(InvalidFilterException.class,
                () -> validator.validate(filtro("semestre-2", null, null)));
        assertDoesNotThrow(() -> validator.validate(filtro("2026-2", null, null)));
    }

    @Test
    void deberiaRechazarUnSizeFueraDeRango() {
        assertThrows(InvalidFilterException.class,
                () -> validator.validatePage(new PageFilter(0, 0, null)));
        assertThrows(InvalidFilterException.class,
                () -> validator.validatePage(new PageFilter(0, 500, null)));
    }

    @Test
    void deberiaRechazarUnaPaginaNegativaYUnOrdenInvalido() {
        assertThrows(InvalidFilterException.class,
                () -> validator.validatePage(new PageFilter(-1, 10, null)));
        assertThrows(InvalidFilterException.class,
                () -> validator.validatePage(new PageFilter(0, 10, "ALFABETICO")));
        assertDoesNotThrow(() -> validator.validatePage(new PageFilter(null, null, null)));
    }
}