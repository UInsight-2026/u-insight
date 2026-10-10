package gt.edu.uinsight.report.dto.response;

/**
 * Nivel de riesgo de una seccion segun la Celula B7, en el contrato de C5.
 *
 * riskLevel queda en null cuando no se pudo calcular. No se sustituye por "LOW":
 * decir que una seccion esta en riesgo bajo cuando en realidad no se sabe es peor
 * que no responder.
 *
 * riskSource indica con cuanta informacion se calculo:
 *   B7          el motor evaluo todas sus reglas
 *   B7_PARTIAL  el motor respondio, pero C5 no pudo alimentar todas sus entradas
 *   NONE        no hay nivel de riesgo
 */
public class RiskSnapshot {

    private final String riskLevel;   // LOW | MEDIUM | HIGH | null
    private final String riskSource;  // B7 | B7_PARTIAL | NONE
    private final boolean available;

    public RiskSnapshot(String riskLevel, String riskSource, boolean available) {
        this.riskLevel = riskLevel;
        this.riskSource = riskSource;
        this.available = available;
    }

    public static RiskSnapshot unavailable() {
        return new RiskSnapshot(null, "NONE", false);
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getRiskSource() {
        return riskSource;
    }

    public boolean isAvailable() {
        return available;
    }
}
