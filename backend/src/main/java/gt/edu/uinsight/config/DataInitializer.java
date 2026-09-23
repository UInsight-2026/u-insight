package gt.edu.uinsight.config;

import gt.edu.uinsight.system.model.SystemCheck;
import gt.edu.uinsight.system.repository.SystemCheckRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initSystemChecks(SystemCheckRepository repository) {
        return args -> {
            // Solo inserta si la tabla está vacía
            if (repository.count() == 0) {
                repository.save(new SystemCheck("database", "UP"));
                repository.save(new SystemCheck("redis", "DOWN"));
                repository.save(new SystemCheck("api-gateway", "DEGRADED"));
                repository.save(new SystemCheck("payment-gateway", "UP"));
                
                System.out.println("✅ Datos semilla de SystemCheck insertados correctamente.");
            }
        };
    }
}