package gt.edu.uinsight.alert.b7.service;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import gt.edu.uinsight.alert.b7.model.AlertRule;
import gt.edu.uinsight.alert.b7.model.RiskInput; 
import gt.edu.uinsight.alert.b7.model.RiskOutput;
    
@Service 
public class RiskEngineService {    
  public RiskOutput evaluarRiesgo() {   
  
    RiskInput input = RiskInput.pruebas(); 

    if (input == null ||
        input.getMedia() <= 0 ||
        input.getDesviacion() < 0 ||
        input.getTendencia() == 0 ||
        input.getPercentil90() <= 0) {

        return new RiskOutput(
                "SIN_DATOS",
                "No se puede evaluar riesgo por falta de información",
                0,
                0,
                0,
                0
        );
    }
    
    List<AlertRule> rules = getMockRules();
    int rulesEvaluated = rules.size();
    int rulesTriggered = 0;
    for (AlertRule rule : rules) {
      if (evaluateRule(rule, input)) {
        rulesTriggered++;        
      }
    }

    int alertsGenerated = rulesTriggered;
    double score = input.getMedia()+ input.getDesviacion()+ input.getTendencia();
    String nivel = (rulesTriggered == 0) ? "BAJO": (rulesTriggered == 1) ? "MODERADO": "ALTO";
    String alerta = (rulesTriggered == 0) ? "Sin alerta":(rulesTriggered == 1) ? "Revisión recomendada": "Alerta crítica";
    return new RiskOutput(nivel, alerta, score, rulesEvaluated,rulesTriggered, alertsGenerated);
  }

    private List<AlertRule> getMockRules() {
        List<AlertRule> rules = new ArrayList<>();

        AlertRule r1 = new AlertRule();
        r1.setId(1L);
        r1.setNombre("Media baja y tendencia negativa");
        r1.setConditionExp("average < 15 AND trend == 'NEGATIVE'");
        r1.setSeveridad("HIGH");
        r1.setActive(true);

        rules.add(r1);

        AlertRule r2 = new AlertRule();
        r2.setId(2L);
        r2.setNombre("Desviación alta");
        r2.setConditionExp("deviation > 5");
        r2.setSeveridad("MEDIUM");
        r2.setActive(true);
        rules.add(r2);

        AlertRule r3 = new AlertRule();
        r3.setId(3L);
        r3.setNombre("Percentil 90 crítico");
        r3.setConditionExp("percentile90 < 20");
        r3.setSeveridad("LOW");
        r3.setActive(true);
        rules.add(r3);

        return rules;
    }

    
    private boolean evaluateRule(AlertRule rule, RiskInput input) {
        String expr = rule.getConditionExp();
        boolean averageLow = expr.contains("average <") && input.getMedia() < extractNumber(expr, "average <");
        boolean deviationHigh = expr.contains("deviation >") && input.getDesviacion() > extractNumber(expr, "deviation >");
        boolean trendNegative = expr.contains("trend == 'NEGATIVE'") && input.getTendencia() < 0;
        boolean percentileCritical = expr.contains("percentile90 <") && input.getPercentil90() < extractNumber(expr, "percentile90 <");
        return averageLow || deviationHigh || trendNegative || percentileCritical;
    }

    private double extractNumber(String expr, String key) {
        try {
            String part = expr.substring(expr.indexOf(key) + key.length()).trim();
            return Double.parseDouble(part.split(" ")[0]);
        } catch (Exception e) {
            return Double.MAX_VALUE;
        }
    }
}  


