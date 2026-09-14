package gt.edu.uinsight.alert.b7.service;

import org.springframework.stereotype.Service;
import gt.edu.uinsight.alert.b7.model.RiskInput;
import gt.edu.uinsight.alert.b7.model.RiskOutput;

@Service
public class RiskEngineService {

    public RiskOutput evaluarRiesgo() {
        RiskInput input = RiskInput.pruebas();

        double score = input.getMedia() + input.getDesviacion() + input.getTendencia();

        if (score < 15) {
            return new RiskOutput("BAJO", "Sin alerta", score);
        } else if (score < 25) {
            return new RiskOutput("MODERADO", "Revisión recomendada", score);
        } else {
            return new RiskOutput("ALTO", "Alerta crítica", score);
        }
    }
}