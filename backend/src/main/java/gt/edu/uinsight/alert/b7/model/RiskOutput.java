package gt.edu.uinsight.alert.b7.model;

public class RiskOutput {
    private String nivelRiesgo;
    private String alerta;
    private double valorCalculado;

    public RiskOutput(String nivelRiesgo, String alerta, double valorCalculado) {
        this.nivelRiesgo = nivelRiesgo;
        this.alerta = alerta;
        this.valorCalculado = valorCalculado;
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

    public void setNivelRiesgo(String nivelRiesgo){
        this.nivelRiesgo = nivelRiesgo;
    }

    public void setAlerta(String alerta){
        this.alerta = alerta;
    }

    public void setValorCalculado(double valorCalculado){
        this.valorCalculado = valorCalculado;
    }
}