package gt.edu.uinsight.integration.c1;

import org.springframework.stereotype.Component;

@Component
public class C1ConfigurationClient {

    /**
     * Simula la consulta del umbral de rendimiento enviado por la Célula C1
     */
    public Double getLowPerformanceThreshold() {
        return 60.0; 
    }
}
