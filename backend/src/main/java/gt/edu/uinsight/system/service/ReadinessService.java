package gt.edu.uinsight.system.service;

import gt.edu.uinsight.system.dto.response.ReadinessResponse;
import gt.edu.uinsight.system.entity.CheckStatus;
import gt.edu.uinsight.system.logging.SystemEventLogger;
import gt.edu.uinsight.system.repository.SystemCheckLogRepository;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReadinessService {

    /** Puerto que usa Spring Boot cuando {@code server.port} no esta declarado. */
    private static final String PUERTO_POR_DEFECTO = "8080";

    /** Ruta que usa springdoc cuando la propiedad no esta declarada. */
    private static final String RUTA_SWAGGER_POR_DEFECTO = "/swagger-ui.html";

    private static final String OK = ": OK";
    private static final String FALTA = ": MISSING";
    private static final String INVALIDO = ": INVALIDO";

    private final DatabaseHealthIndicator databaseHealthIndicator;
    private final Environment environment;
    private final SystemCheckLogRepository systemCheckLogRepository;
    private final IntegrationStatusService integrationStatusService;
    private final SystemEventLogger systemEventLogger;

    public ReadinessService(
            DatabaseHealthIndicator databaseHealthIndicator,
            Environment environment,
            SystemCheckLogRepository systemCheckLogRepository,
            IntegrationStatusService integrationStatusService,
            SystemEventLogger systemEventLogger) {

        this.databaseHealthIndicator = databaseHealthIndicator;
        this.environment = environment;
        this.systemCheckLogRepository = systemCheckLogRepository;
        this.integrationStatusService = integrationStatusService;
        this.systemEventLogger = systemEventLogger;
    }

    public ReadinessResponse checkReadiness() {

        CheckStatus database = databaseHealthIndicator.check();

        List<String> configurationDetails = checkConfiguration();
        CheckStatus configuration = configurationDetails.stream()
                .anyMatch(detail -> detail.contains(FALTA) || detail.contains(INVALIDO))
                ? CheckStatus.DOWN
                : CheckStatus.UP;

        List<String> criticalServicesDetails = checkCriticalServices();
        CheckStatus criticalServices = criticalServicesDetails.stream()
                .anyMatch(detail -> detail.endsWith(": DOWN"))
                ? CheckStatus.DOWN
                : CheckStatus.UP;

        boolean ready = database == CheckStatus.UP
                && configuration == CheckStatus.UP
                && criticalServices == CheckStatus.UP;

        ReadinessResponse response = new ReadinessResponse(
                ready,
                database,
                configuration,
                criticalServices,
                configurationDetails,
                criticalServicesDetails
        );

        if (!ready) {
            systemEventLogger.warn(
                    "READINESS_CHECK",
                    503,
                    "Readiness check failed: " + buildFailureMessage(
                            database,
                            configuration,
                            criticalServices
                    )
            );
        }

        return response;
    }

    /**
     * Verifica las cuatro propiedades criticas del modulo.
     *
     * <p>Las dos del datasource son obligatorias: sin ellas el modulo no opera y su
     * ausencia es un fallo real. Las otras dos tienen valor por defecto en el framework,
     * asi que <strong>ausente no es lo mismo que mal configurado</strong>: se resuelven
     * contra ese valor y se valida el resultado.
     *
     * <p>Exigirlas declaradas daba un falso negativo. En el perfil de test,
     * {@code src/test/resources/application.properties} tapa al de {@code main} -no se
     * fusionan, Spring Boot resuelve un unico recurso y gana el de {@code test-classes}- y
     * no declara ni {@code server.port} ni {@code springdoc.swagger-ui.path}. Readiness
     * devolvia 503 con la aplicacion sana, y la prueba E2E que espera 200 no podia pasar.
     */
    private List<String> checkConfiguration() {

        List<String> details = new ArrayList<>();

        checkObligatoria("spring.datasource.url", details);
        checkObligatoria("spring.datasource.username", details);
        checkPuerto("server.port", details);
        checkRuta("springdoc.swagger-ui.path", details);

        return details;
    }

    private void checkObligatoria(String propiedad, List<String> details) {

        String valor = environment.getProperty(propiedad);

        if (valor == null || valor.isBlank()) {
            details.add(propiedad + FALTA);
        } else {
            details.add(propiedad + OK);
        }
    }

    private void checkPuerto(String propiedad, List<String> details) {

        String declarado = environment.getProperty(propiedad);
        String valor = esVacio(declarado) ? PUERTO_POR_DEFECTO : declarado.trim();

        int puerto;

        try {
            puerto = Integer.parseInt(valor);
        } catch (NumberFormatException exception) {
            details.add(propiedad + INVALIDO + " (" + valor + " no es un numero)");
            return;
        }

        // El 0 es valido: Spring Boot lo usa para pedir un puerto libre al sistema.
        if (puerto < 0 || puerto > 65535) {
            details.add(propiedad + INVALIDO + " (" + puerto + " fuera de 0-65535)");
            return;
        }

        details.add(propiedad + OK + sufijoPorDefecto(declarado, valor));
    }

    private void checkRuta(String propiedad, List<String> details) {

        String declarado = environment.getProperty(propiedad);
        String valor = esVacio(declarado) ? RUTA_SWAGGER_POR_DEFECTO : declarado.trim();

        if (!valor.startsWith("/")) {
            details.add(propiedad + INVALIDO + " (" + valor + " no empieza con /)");
            return;
        }

        details.add(propiedad + OK + sufijoPorDefecto(declarado, valor));
    }

    /** Deja constancia en la respuesta de que el valor no venia declarado. */
    private String sufijoPorDefecto(String declarado, String valor) {

        return esVacio(declarado) ? " (valor por defecto " + valor + ")" : "";
    }

    private static boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private List<String> checkCriticalServices() {

        List<String> details = new ArrayList<>();

        try {
            systemCheckLogRepository.count();
            details.add("SystemCheckLogRepository: OK");
        } catch (Exception exception) {
            details.add("SystemCheckLogRepository: DOWN");
        }

        try {
            if (integrationStatusService != null) {
                details.add("IntegrationStatusService: OK");
            } else {
                details.add("IntegrationStatusService: DOWN");
            }
        } catch (Exception exception) {
            details.add("IntegrationStatusService: DOWN");
        }

        return details;
    }

    private String buildFailureMessage(
            CheckStatus database,
            CheckStatus configuration,
            CheckStatus criticalServices) {

        List<String> failures = new ArrayList<>();

        if (database == CheckStatus.DOWN) {
            failures.add("database");
        }

        if (configuration == CheckStatus.DOWN) {
            failures.add("configuration");
        }

        if (criticalServices == CheckStatus.DOWN) {
            failures.add("criticalServices");
        }

        return String.join(", ", failures);
    }
}