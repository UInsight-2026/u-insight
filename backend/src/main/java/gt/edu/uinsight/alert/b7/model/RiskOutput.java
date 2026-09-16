package gt.edu.uinsight.alert.b7.model;

public class RiskOutput {
    private String nivelRiesgo;
    private String alerta;
    private double valorCalculado;
    private int reglasEvaluadas;
    private int reglasActivadas;
    private int alertasGeneradas;

    public RiskOutput(String nivelRiesgo, String alerta, double valorCalculado, int reglasEvaluadas, int reglasActivadas, int alertasGeneradas) {
        this.nivelRiesgo = nivelRiesgo;
        this.alerta = alerta;
        this.valorCalculado = valorCalculado;
        this.reglasEvaluadas = reglasEvaluadas;
        this.reglasActivadas = reglasActivadas;
        this.alertasGeneradas = alertasGeneradas;
    }

    public String getNivelRiesgo(){
        return nivelRiesgo;
    }

    public String getAlerta(){
        return alerta;
    }

    public double getValorCalculado(){
        return valorCalculado;
    }

    public int getReglasEvaluadas(){
        return reglasEvaluadas;
    }

    public int getReglasActivadas(){
        return reglasActivadas;
    }

    public int getAlertasGeneradas(){
        return alertasGeneradas;
    }

    public void setNivelRiesgo(String nivelRiesgo){
        this.nivelRiesgo = nivelRiesgo;
    }

    public void setAlerta(String alerta){
        this.alerta = alerta;
    }

    public void setValorCalculado(double valorCalculado){
        this.valorCalculado = valorCalculado;
    }

    public void setReglasEvaluadas(int reglasEvaluadas){
        this.reglasEvaluadas = reglasEvaluadas;
    }

    public void setReglasActivadas(int reglasActivadas){
        this.reglasActivadas = reglasActivadas;
    }

    public void setAlertasGeneradas(int alertasGeneradas){
        this.alertasGeneradas= alertasGeneradas;
    }
}