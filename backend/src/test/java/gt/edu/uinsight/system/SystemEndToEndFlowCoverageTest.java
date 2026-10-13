
package gt.edu.uinsight.system;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * U-Insight - Celula C7 - Semana 5.
 *
 * Matriz de cobertura del flujo oficial de 14 pasos.
 *
 * Registra los resultados observados en SystemEndToEndFlowTest
 * y los bloqueos identificados durante las pruebas.
 *
 * IMPORTANTE:
 * Esta clase valida la consistencia de la matriz, pero no
 * ejecuta nuevamente los endpoints ni verifica sus respuestas.
 *
 * Evidencia de referencia: ejecucion E2E del 09/10/2026,
 * con 2 pruebas JUnit y BUILD SUCCESS.
 *
 * El antiguo DuplicateMappingException de B3 ya no bloquea
 * el arranque del contexto de Spring.
 */
class SystemEndToEndFlowCoverageTest {

        /**
         * Estados utilizados para clasificar cada paso:
         *
         * PASA:
         * Endpoint ejecutado con el resultado esperado.
         *
         * PARCIAL:
         * Endpoint ejecutado, pero con datos o integracion incompleta.
         *
         * BLOQUEADO:
         * Endpoint disponible, pero no se pudo completar
         * el flujo por una dependencia conocida.
         *
         * NO DISPONIBLE:
         * No se identifico un endpoint REST implementado.
         *
         * NO EJECUTADO:
         * No se realizo una llamada al endpoint.
         */
        private static final Set<String> ESTADOS_VALIDOS = Set.of(
                        "PASA",
                        "PARCIAL",
                        "BLOQUEADO",
                        "NO DISPONIBLE",
                        "NO EJECUTADO");

        /**
         * Formato:
         * paso | celula | endpoint | estado | detalle
         *
         * Los resultados se basan en la ultima ejecucion E2E
         * y en la inspeccion de los controladores disponibles.
         */
        private static final String[] COBERTURA = {

                        // ====================================================
                        // FLUJO ACADEMICO
                        // ====================================================

                        "01 | A1 | POST /api/v1/academic-periods | PASA | "
                                        + "HTTP 201; periodo academico creado y se obtuvo su ID",

                        "02 | A1 | POST /api/v1/courses | PASA | "
                                        + "HTTP 201; curso creado y se obtuvo su ID",

                        "03 | A2 | POST /api/v1/teachers | PASA | "
                                        + "HTTP 201; docente creado y se obtuvo su ID",

                        "04 | A3 | POST /api/v1/students | NO DISPONIBLE | "
                                        + "No se identifico un controlador REST de creacion de estudiantes; "
                                        + "el flujo utiliza studentId=1001 como dato de prueba",

                        "05 | A4 | POST /api/v1/sections | PASA | "
                                        + "HTTP 201; seccion creada con academicPeriodId, courseId y teacherId",

                        "06 | A4 | POST /api/v1/sections/{id}/enrollments | PARCIAL | "
                                        + "HTTP 201; inscripcion creada con studentId=1001, "
                                        + "pero A4 no comprueba su existencia contra A3",

                        "07 | A5 | POST /api/v1/evaluations | PASA | "
                                        + "HTTP 201; evaluacion creada y se obtuvo evaluationId",

                        "08 | A6 | POST /api/v1/grades | PARCIAL | "
                                        + "HTTP 201; calificacion registrada, pero requirio cargar "
                                        + "referencias auxiliares mediante /grades/evaluation-refs "
                                        + "y /grades/enrollment-refs; no hay sincronizacion automatica",

                        // ====================================================
                        // ANALITICA
                        // ====================================================

                        "09 | B1/B2/B3/B5/B6 | GET /api/v1/analytics/sections/{id}/summary | PARCIAL | "
                                        + "HTTP 206; fallaron los componentes de tendencia central "
                                        + "y dispersion; tendencia sin datos y studentsAtRisk sin integrar",

                        "10 | B4 | GET /api/v1/analytics/sections/{id}/trends | BLOQUEADO | "
                                        + "HTTP 404; el servicio informa TREND_CALCULATION_NO_DATA "
                                        + "para la seccion creada durante el E2E",

                        // ====================================================
                        // RIESGO Y ALERTAS
                        // ====================================================

                        "11 | B7 | POST /api/v1/risk-evaluation/sections/{sectionId} | PARCIAL | "
                                        + "HTTP 200; nivel MEDIUM, score=35, cinco reglas evaluadas "
                                        + "y dos activadas; se utilizaron indicadores simulados, "
                                        + "no resultados obtenidos automaticamente de B1-B6",

                        "12 | C3 | GET /api/v1/alerts/{id} | NO DISPONIBLE | "
                                        + "No se identifico el endpoint individual de C3; "
                                        + "como alternativa se ejecuto GET /api/v1/reports/alerts de C5 "
                                        + "con HTTP 200 y 16 alertas de otras secciones",

                        // ====================================================
                        // INTERVENCIONES Y SEGUIMIENTOS
                        // ====================================================

                        "13 | C4 | POST /api/v1/alerts/{id}/interventions | BLOQUEADO | "
                                        + "Endpoint implementado, pero no ejecutado; "
                                        + "no se obtuvo un alertId persistido y validado "
                                        + "para la seccion E2E",

                        "14 | C4 | POST /api/v1/interventions/{id}/follow-ups | NO DISPONIBLE | "
                                        + "FollowUpController contiene solo comentarios; "
                                        + "no implementa un controlador REST ni sus mappings",

                        // ====================================================
                        // ENDPOINTS PROPIOS DE C7
                        // ====================================================

                        "C7 | C7 | GET /api/v1/system/readiness | PASA | "
                                        + "HTTP 200 y respuesta JSON; verificado mediante MockMvc",

                        "C7 | C7 | GET /api/v1/system/integration-status | PARCIAL | "
                                        + "HTTP 200 y respuesta JSON; los registros informan "
                                        + "multiples modulos DOWN durante las comprobaciones"
        };

        private static final int PASOS_DEL_FLUJO = 14;
        private static final int ENDPOINTS_C7 = 2;

        @Test
        void laTablaDeCoberturaEstaBienFormadaYSeImprime() {

                assertEquals(
                                PASOS_DEL_FLUJO + ENDPOINTS_C7,
                                COBERTURA.length,
                                "Deben existir 14 pasos del flujo y 2 endpoints C7");

                System.out.println();
                System.out.println(
                                "TABLA DE COBERTURA - U-INSIGHT - CELULA C7 - SEMANA 5");

                System.out.println("=".repeat(140));

                System.out.printf(
                                "%-4s | %-16s | %-53s | %-14s | %s%n",
                                "PASO",
                                "CELULA",
                                "ENDPOINT",
                                "ESTADO",
                                "DETALLE");

                System.out.println("-".repeat(140));

                for (String fila : COBERTURA) {

                        String[] campos = camposDe(fila);

                        assertEquals(
                                        5,
                                        campos.length,
                                        "Cada fila debe contener cinco campos: " + fila);

                        assertTrue(
                                        ESTADOS_VALIDOS.contains(campos[3]),
                                        "Estado no reconocido: " + campos[3]);

                        assertTrue(
                                        campos[4].length() > 10,
                                        "El detalle debe describir el resultado: " + fila);

                        System.out.printf(
                                        "%-4s | %-16s | %-53s | %-14s | %s%n",
                                        campos[0],
                                        campos[1],
                                        campos[2],
                                        campos[3],
                                        campos[4]);
                }

                System.out.println("=".repeat(140));
        }

        @Test
        void elResumenDeCoberturaCoincideConLosResultadosObservados() {

                Map<String, Integer> porEstado = new LinkedHashMap<>();

                for (String fila : COBERTURA) {
                        String estado = camposDe(fila)[3];
                        porEstado.merge(estado, 1, Integer::sum);
                }

                System.out.println();
                System.out.println("RESUMEN DE COBERTURA - SEMANA 5");

                porEstado.forEach((estado, total) -> System.out.printf(
                                "  %-14s : %2d%n",
                                estado,
                                total));

                System.out.println();

                // Clasificacion de las 16 filas de la matriz.
                assertEquals(
                                16,
                                porEstado.values()
                                                .stream()
                                                .mapToInt(Integer::intValue)
                                                .sum(),
                                "Todas las filas deben estar clasificadas");

                assertEquals(
                                6,
                                porEstado.getOrDefault("PASA", 0),
                                "La matriz debe conservar los seis casos "
                                                + "clasificados como PASA");

                assertEquals(
                                5,
                                porEstado.getOrDefault("PARCIAL", 0),
                                "La matriz debe conservar los cinco pasos parciales");

                assertEquals(
                                2,
                                porEstado.getOrDefault("BLOQUEADO", 0),
                                "La matriz debe conservar los dos bloqueos");

                assertEquals(
                                3,
                                porEstado.getOrDefault("NO DISPONIBLE", 0),
                                "La matriz debe conservar los tres endpoints "
                                                + "no disponibles");

                assertEquals(
                                0,
                                porEstado.getOrDefault("NO EJECUTADO", 0),
                                "Los casos no ejecutados deben estar descritos "
                                                + "como bloqueos o falta de disponibilidad");
        }

        /**
         * Separa una fila por delimitadores verticales.
         * Devuelve los cinco campos sin espacios sobrantes.
         */
        private static String[] camposDe(String fila) {

                String[] campos = fila.split(Pattern.quote("|"), -1);

                for (int i = 0; i < campos.length; i++) {
                        campos[i] = campos[i].trim();
                }

                return campos;
        }
}
