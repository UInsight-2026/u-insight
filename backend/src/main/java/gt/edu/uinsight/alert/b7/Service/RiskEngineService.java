package gt.edu.uinsight.alert.b7.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import gt.edu.uinsight.alert.b7.model.AlertRule;
import gt.edu.uinsight.alert.b7.model.RiskInput; 
import gt.edu.uinsight.alert.b7.model.RiskOutput;
    
@Service 
public class RiskEngineService {    
  
    private Map<String, Double> toMap(RiskInput input) {
        Map<String, Double> map = new HashMap<>();
        map.put("media", input.getMedia());
        map.put("mediana", input.getMediana());
        map.put("desviacion", input.getDesviacion());
        map.put("tendencia", input.getTendencia());
        map.put("percentil90", input.getPercentil90());
        return map;
    }


    private boolean evaluateExpression(String exp, Map<String, Double> values) {

        String[] parts = exp.split("&&");
        for (String part : parts) {
            part = part.trim();
            String operator = null;
            if (part.contains("<=")) operator = "<=";
            else if (part.contains(">=")) operator = ">=";
            else if (part.contains("==")) operator = "==";
            else if (part.contains("<")) operator = "<";
            else if (part.contains(">")) operator = ">";

            if (operator == null) return false;
            String[] tokens = part.split(operator);
            String field = tokens[0].trim();
            double threshold = Double.parseDouble(tokens[1].trim());
            Double value = values.get(field);
            if (value == null) return false;

            boolean result = switch (operator) {
                case "<"  -> value < threshold;
                case ">"  -> value > threshold;
                case "<=" -> value <= threshold;
                case ">=" -> value >= threshold;
                case "==" -> value == threshold;
                default   -> false;
            };
            if (!result) return false;
        }

        return true;
    }

    private int evaluateRules(RiskInput input, List<AlertRule> rules) {
        Map<String, Double> values = toMap(input);
        int triggered = 0;

        for (AlertRule rule : rules) {
            if (!rule.isActive()) continue;

            boolean match = evaluateExpression(rule.getConditionExp(), values);

            if (match) {
                triggered++;
            }
        }

        return triggered;
    }

    private List<AlertRule> getAlertasActivadas(RiskInput input, List<AlertRule> rules) {
        Map<String, Double> values = toMap(input);
        List<AlertRule> activadas = new ArrayList<>();

        for (AlertRule rule : rules) {
            if (!rule.isActive()) continue;

            boolean match = evaluateExpression(rule.getConditionExp(), values);
            if (match) activadas.add(rule);
        }

        return activadas;
    }

    public RiskOutput evaluarRiesgo(RiskInput input) {

        List<AlertRule> rules = getMockRules();
        int rulesEvaluated = rules.size();
        List<AlertRule> alertasActivadas = getAlertasActivadas(input, rules);
        int rulesTriggered = evaluateRules(input, rules);
        int alertsGenerated = rulesTriggered;

        double score = input.getMedia() + input.getDesviacion() + input.getTendencia();

        String nivel = (rulesTriggered == 0) ? "BAJO"
                       : (rulesTriggered == 1) ? "MODERADO"
                       : "ALTO";

        String alerta = (rulesTriggered == 0) ? "Sin alerta"
                        : (rulesTriggered == 1) ? "Revisión recomendada"
                        : "Alerta crítica";

        return new RiskOutput(nivel, alerta, score, rulesEvaluated, rulesTriggered, alertsGenerated, alertasActivadas);
    }

    private List<AlertRule> getMockRules() {
        List<AlertRule> rules = new ArrayList<>();

        AlertRule r1 = new AlertRule();
        r1.setId(1L);
        r1.setNombre("Media baja y tendencia negativa");
        r1.setConditionExp("media < 60 && tendencia < 0");
        r1.setSeveridad("HIGH");
        r1.setActive(true);
        rules.add(r1);

        AlertRule r2 = new AlertRule();
        r2.setId(2L);
        r2.setNombre("Desviación alta");
        r2.setConditionExp("desviacion > 5");
        r2.setSeveridad("MEDIUM");
        r2.setActive(true);
        rules.add(r2);

        AlertRule r3 = new AlertRule();
        r3.setId(3L);
        r3.setNombre("Percentil 90 crítico");
        r3.setConditionExp("percentil90 < 20");
        r3.setSeveridad("LOW");
        r3.setActive(true);
        rules.add(r3);

        return rules;
    }
}  


