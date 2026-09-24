package gt.edu.uinsight.system.config;

import gt.edu.uinsight.system.entity.CheckStatus;
import gt.edu.uinsight.system.entity.SystemCheckLog;
import gt.edu.uinsight.system.repository.SystemCheckLogRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Datos semilla del modulo system. La base es H2 en memoria, asi que sin ellos
 * el listado arranca vacio y no hay nada que filtrar ni paginar.
 * Vive en el paquete del modulo, no en el config compartido, para no chocar con
 * los inicializadores de las demas celulas.
 */
@Configuration
public class SystemCheckDataInitializer {

    @Bean
    public CommandLineRunner initSystemChecks(SystemCheckLogRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            repository.save(buildCheck("database", CheckStatus.UP, "Conexion verificada al arranque"));
            repository.save(buildCheck("database", CheckStatus.DEGRADED, "Latencia por encima de lo esperado"));
            repository.save(buildCheck("swagger-ui", CheckStatus.UP, "Documentacion disponible"));
            repository.save(buildCheck("integration-status", CheckStatus.UP, "Modulos de otras celulas consultados"));
            repository.save(buildCheck("alerts", CheckStatus.DOWN, "Depende de la tabla alert, aun no entregada"));
        };
    }

    private SystemCheckLog buildCheck(String component, CheckStatus status, String message) {
        SystemCheckLog log = new SystemCheckLog();
        log.setComponent(component);
        log.setStatus(status);
        log.setMessage(message);
        return log;
    }
}
