package gt.edu.uinsight.alert.b7.model;

import java.util.List;

public class RiskOutput {
    private String nivelRiesgo;
    private String alerta;
    private double valorCalculado;
    private int reglasEvaluadas;
    private int reglasActivadas;
    private int alertasGeneradas;
    private List<AlertRule> alertas;

    public RiskOutput(String nivelRiesgo, String alerta, double valorCalculado,int reglasEvaluadas, int reglasActivadas, int alertasGeneradas, List<AlertRule> alertas) {
        this.nivelRiesgo = nivelRiesgo;
        this.alerta = alerta;
        this.valorCalculado = valorCalculado;
        this.reglasEvaluadas = reglasEvaluadas;
        this.reglasActivadas = reglasActivadas;
        this.alertasGeneradas = alertasGeneradas;
        this.alertas = alertas;
    }

    public String getNivelRiesgo() { 
        return nivelRiesgo; 
    }
    public String getAlerta() { 
        return alerta; 
    }
    
    public double getValorCalculado() { 
        return valorCalculado; 
    }

    public int getReglasEvaluadas() { 
        return reglasEvaluadas; 
    }

    public int getReglasActivadas() { 
        return reglasActivadas; 
    }

    public int getAlertasGeneradas() { 
        return alertasGeneradas; 
    }

    public List<AlertRule> getAlertas() {
        return alertas; 
    }

    public void setAlertas(List<AlertRule> alertas) {
        this.alertas = alertas;
    }
}