package gt.edu.uinsight.system;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tabla de cobertura del flujo oficial de 14 pasos (seccion 7.3 del documento oficial).
 *
 * <p>Esta clase <strong>no</strong> levanta el contexto de Spring, y es a proposito. El
 * recorrido real de los endpoints vive en {@link SystemEndToEndFlowTest}, que hoy esta
 * deshabilitada porque el contexto no arranca. Si la tabla viviera ahi se caeria con ella,
 * y la tabla es justo la evidencia que el plan de la semana pide capturar: no vale que el
 * flujo pase entero, vale saber en que paso se corta y de quien es.
 */
class SystemEndToEndFlowCoverageTest {

    /**
     * Bloqueo vigente en la rama: Hibernate no puede construir el EntityManagerFactory.
     * Reportado a la celula B3, no corregido por C7.
     */
    private static final String BLOQUEO_B3 =
            "DuplicateMappingException (B3, PR #104): DispersionEvaluation y Evaluation "
            + "comparten el nombre de entidad 'DispersionEvaluation'; el contexto no arranca";

    private static final String SIN_ENDPOINT =
            "El documento oficial define el endpoint, pero no esta implementado en develop";

    /** Vocabulario cerrado de estados, para que la tabla no se vuelva texto libre. */
    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("PASA", "BLOQUEADO", "NO DISPONIBLE", "NO EJECUTADO");

    /** Formato de cada fila: {@code paso | celula | endpoint | estado | detalle}. */
    private static final String[] COBERTURA = {
        "01 | A1 | POST /api/v1/academic-periods | BLOQUEADO | " + BLOQUEO_B3,
        "02 | A1 | POST /api/v1/courses | NO DISPONIBLE | " + SIN_ENDPOINT,
        "03 | A2 | POST /api/v1/teachers | BLOQUEADO | " + BLOQUEO_B3,
        "04 | A3 | POST /api/v1/students | NO DISPONIBLE | " + SIN_ENDPOINT,
        "05 | A4 | POST /api/v1/sections | NO EJECUTADO | Requiere courseId del paso 02",
        "06 | A4 | POST /api/v1/sections/{id}/enrollments | NO EJECUTADO | Requiere sectionId del paso 05 y studentId del paso 04",
        "07 | A5 | POST /api/v1/evaluations | NO EJECUTADO | Requiere sectionId del paso 05",
        "08 | A6 | POST /api/v1/grades | NO EJECUTADO | Requiere evaluationId del paso 07 y studentId del paso 04",
        "09 | B1/B2/B3/B5/B6 | GET /api/v1/analytics/sections/{id}/summary | NO EJECUTADO | Requiere las calificaciones del paso 08",
        "10 | B4 | GET /api/v1/analytics/sections/{id}/trends | NO EJECUTADO | Requiere las calificaciones del paso 08; la ruta real es /trends en plural, no /trend",
        "11 | B7 | POST /api/v1/alert/b7/evaluar | NO EJECUTADO | Requiere el analisis del paso 09 y la tendencia del paso 10",
        "12 | C3 | GET /api/v1/alerts/{id} | NO DISPONIBLE | No hay recurso de alertas: C3 solo expone /api/v1/alert-rules y /api/v1/reports/alerts es un reporte de C5",
        "13 | C4 | POST /api/v1/alerts/{id}/interventions | NO EJECUTADO | Requiere alertId del paso 12",
        "14 | C4 | POST /api/v1/interventions/{id}/follow-ups | NO DISPONIBLE | FollowUpController existe pero es un esqueleto: declara el paquete y documenta los endpoints en comentarios, sin @RestController ni mappings",
        "C7 | C7 | GET /api/v1/system/readiness | BLOQUEADO | " + BLOQUEO_B3,
        "C7 | C7 | GET /api/v1/system/integration-status | BLOQUEADO | " + BLOQUEO_B3
    };

    private static final int PASOS_DEL_FLUJO = 14;
    private static final int PASOS_DE_C7 = 2;

    @Test
    void laTablaDeCoberturaEstaBienFormadaYSeImprime() {

        assertEquals(
                PASOS_DEL_FLUJO + PASOS_DE_C7,
                COBERTURA.length,
                "La tabla debe cubrir los 14 pasos del flujo oficial mas los 2 de C7");

        System.out.println();
        System.out.println("TABLA DE COBERTURA DEL FLUJO OFICIAL - CELULA C7, SEMANA 4");
        System.out.println("=".repeat(120));
        System.out.printf("%-4s | %-15s | %-46s | %-13s | %s%n",
                "PASO", "CELULA", "ENDPOINT", "ESTADO", "DETALLE");
        System.out.println("-".repeat(120));

        for (String fila : COBERTURA) {

            String[] campos = camposDe(fila);

            assertEquals(5, campos.length,
                    "Cada fila debe tener paso, celula, endpoint, estado y detalle: " + fila);

            assertTrue(ESTADOS_VALIDOS.contains(campos[3]),
                    "Estado no reconocido '" + campos[3] + "' en la fila: " + fila);

            assertTrue(campos[4].length() > 10,
                    "El detalle debe explicar el motivo, no quedar vacio: " + fila);

            System.out.printf("%-4s | %-15s | %-46s | %-13s | %s%n",
                    campos[0], campos[1], campos[2], campos[3], campos[4]);
        }

        System.out.println("=".repeat(120));
    }

    @Test
    void ningunPasoDelFlujoSeRecorreYElMotivoQuedaRegistrado() {

        Map<String, Integer> porEstado = new LinkedHashMap<>();

        for (String fila : COBERTURA) {
            String estado = camposDe(fila)[3];
            porEstado.merge(estado, 1, Integer::sum);
        }

        System.out.println();
        System.out.println("RESUMEN POR ESTADO");
        porEstado.forEach((estado, total) ->
                System.out.printf("  %-13s : %2d%n", estado, total));
        System.out.println();

        assertEquals(0, porEstado.getOrDefault("PASA", 0),
                "Mientras B3 no corrija el PR #104 ningun paso puede recorrerse; "
                + "cuando este resuelto, actualizar la tabla y este numero");

        assertEquals(PASOS_DEL_FLUJO + PASOS_DE_C7,
                porEstado.values().stream().mapToInt(Integer::intValue).sum(),
                "Todas las filas deben estar clasificadas");
    }

    /** Separa una fila en sus 5 campos sin usar escapes de regex en el literal. */
    private static String[] camposDe(String fila) {

        String[] campos = fila.split(Pattern.quote("|"), -1);

        for (int i = 0; i < campos.length; i++) {
            campos[i] = campos[i].trim();
        }

        return campos;
    }
}
