package gt.edu.uinsight.system.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Cliente HTTP que usa {@code GET /api/v1/system/integration-status} para consultar a las
 * demas celulas.
 *
 * <p>Existe como bean separado para que el timeout de {@link IntegrationProperties} llegue
 * de verdad al cliente. Antes el servicio construia el {@code RestClient} desde el
 * {@code RestClient.Builder} compartido sin aplicar ninguna fabrica, asi que
 * {@code timeoutMs} quedaba declarado y sin efecto: un modulo colgado habria bloqueado el
 * endpoint durante el timeout por defecto del sistema operativo.
 *
 * <p>El bean va <strong>cualificado</strong> a proposito. El proyecto corre con
 * {@code spring.main.allow-bean-definition-overriding=true}: un bean sin cualificar
 * podria terminar inyectado donde no corresponde sin que nadie lo note.
 *
 * <p>El builder se crea con {@link RestClient#builder()} y no se inyecta: Spring Boot 4 ya
 * no autoconfigura un bean {@code RestClient.Builder} con solo tener web en el classpath.
 * Es el mismo camino que tomaron B1 ({@code GradeDataRepositoryImpl}) y B5
 * ({@code A6GradeIntegrationService}) en el PR #131.
 */
@Configuration
public class IntegrationRestClientConfig {

    public static final String QUALIFIER = "integrationRestClient";

    @Bean
    @Qualifier(QUALIFIER)
    public RestClient integrationRestClient(IntegrationProperties properties) {

        int timeout = (int) Math.min(properties.getTimeoutMs(), Integer.MAX_VALUE);

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);

        return RestClient.builder().requestFactory(factory).build();
    }
}
